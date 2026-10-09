package eus.ferpinan.ausolanmenu.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.springframework.stereotype.Service;

import eus.ferpinan.ausolanmenu.cache.MenuCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Extracts the daily menus from the Gasca monthly calendar PDF (in Basque) and stores them in the {@link MenuCache}.
 * <p>
 * Each page contains a grid with one column per weekday (Astelehena ... Ostirala). Every cell starts with the
 * day number, followed by the dishes and ends with a nutritional line ({@code kCal:... P:... Lip:... HC:...}).
 * </p>
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class GascaMenuService {

    private static final List<String> WEEKDAYS =
            List.of("astelehena", "asteartea", "asteazkena", "osteguna", "ostirala");
    private static final Pattern DAY_NUMBER = Pattern.compile("\\d{1,2}");
    private static final Pattern NUTRITION_LINE = Pattern.compile("^kCal:.*");
    private static final float DAY_NUMBER_MIN_FONT_SIZE = 13f;
    private static final float SAME_LINE_TOLERANCE = 0.5f;
    private static final float FONT_SIZE_TOLERANCE = 0.25f;
    private static final float DISH_GAP_FACTOR = 1.35f;
    private static final float DEFAULT_ROW_HEIGHT = 140f;

    private final MenuCache menuCache;

    /**
     * Extracts the daily menus from the PDF and adds them to the cache.
     *
     * @param pdfBytes The raw Gasca calendar PDF.
     * @param month    The month the calendar belongs to.
     * @return The extracted menus keyed by ISO date.
     */
    public Map<String, String> storeMenus(byte[] pdfBytes, YearMonth month) {
        Map<String, String> menus = extractDailyMenus(pdfBytes, month);
        log.info("Extracted {} daily menus from Gasca PDF for {}", menus.size(), month);
        menuCache.putMenus(menus);
        return menus;
    }

    /**
     * Extracts the daily menus from the PDF.
     *
     * @param pdfBytes The raw Gasca calendar PDF.
     * @param month    The month the calendar belongs to.
     * @return The menus keyed by ISO date ({@code yyyy-MM-dd}), one dish per line.
     */
    public Map<String, String> extractDailyMenus(byte[] pdfBytes, YearMonth month) {
        Map<String, String> menus = new TreeMap<>();
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            for (int page = 1; page <= document.getNumberOfPages(); page++) {
                menus.putAll(extractPage(readCharacters(document, page), month));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Error reading Gasca PDF", e);
        }
        return menus;
    }

    private List<TextPosition> readCharacters(PDDocument document, int page) throws IOException {
        List<TextPosition> characters = new ArrayList<>();
        PDFTextStripper stripper = new PDFTextStripper() {
            @Override
            protected void writeString(String text, List<TextPosition> textPositions) {
                textPositions.stream()
                        .filter(position -> position.getDir() == 0)
                        .forEach(characters::add);
            }
        };
        stripper.setStartPage(page);
        stripper.setEndPage(page);
        stripper.getText(document);
        return characters;
    }

    private Map<String, String> extractPage(List<TextPosition> characters, YearMonth month) {
        List<Line> lines = groupIntoLines(characters);
        List<Float> columns = findColumns(lines);
        if (columns.isEmpty()) {
            return Map.of();
        }

        List<Cell> cells = new ArrayList<>();
        for (Line line : lines) {
            for (Word word : line.words()) {
                if (word.fontSize() >= DAY_NUMBER_MIN_FONT_SIZE && DAY_NUMBER.matcher(word.text()).matches()) {
                    int column = columnOf(word.x(), columns);
                    if (column >= 0) {
                        cells.add(new Cell(Integer.parseInt(word.text()), column, line.y()));
                    }
                }
            }
        }

        Map<String, String> menus = new TreeMap<>();
        for (Cell cell : cells) {
            float rowBottom = rowBottom(cell, cells, lines, columns);
            String menu = buildMenu(cell, rowBottom, lines, columns);
            if (menu.isBlank()) {
                continue;
            }
            try {
                LocalDate date = month.atDay(cell.day());
                menus.put(date.format(DateTimeFormatter.ISO_LOCAL_DATE), menu);
            } catch (DateTimeException e) {
                log.warn("Ignoring day {} not valid for {}", cell.day(), month);
            }
        }
        return menus;
    }

    private List<Line> groupIntoLines(List<TextPosition> characters) {
        List<TextPosition> sorted = new ArrayList<>(characters);
        sorted.sort(Comparator.comparingDouble(TextPosition::getYDirAdj));

        List<Line> lines = new ArrayList<>();
        List<TextPosition> current = new ArrayList<>();
        float currentY = Float.NaN;
        for (TextPosition character : sorted) {
            if (!current.isEmpty() && Math.abs(character.getYDirAdj() - currentY) > SAME_LINE_TOLERANCE) {
                lines.add(new Line(currentY, sortByX(current)));
                current = new ArrayList<>();
            }
            if (current.isEmpty()) {
                currentY = character.getYDirAdj();
            }
            current.add(character);
        }
        if (!current.isEmpty()) {
            lines.add(new Line(currentY, sortByX(current)));
        }
        return lines;
    }

    private List<TextPosition> sortByX(List<TextPosition> characters) {
        characters.sort(Comparator.comparingDouble(TextPosition::getXDirAdj));
        return characters;
    }

    private List<Float> findColumns(List<Line> lines) {
        for (Line line : lines) {
            List<Word> words = line.words();
            List<Float> columns = new ArrayList<>();
            for (String weekday : WEEKDAYS) {
                words.stream()
                        .filter(word -> word.text().equalsIgnoreCase(weekday))
                        .findFirst()
                        .ifPresent(word -> columns.add(word.x()));
            }
            if (columns.size() == WEEKDAYS.size()) {
                return columns;
            }
        }
        return List.of();
    }

    private int columnOf(float x, List<Float> columns) {
        float columnWidth = (columns.get(columns.size() - 1) - columns.get(0)) / (columns.size() - 1);
        float margin = columnWidth * 0.05f;
        for (int i = 0; i < columns.size(); i++) {
            float start = columns.get(i) - margin;
            float end = i + 1 < columns.size() ? columns.get(i + 1) - margin : columns.get(i) + columnWidth - margin;
            if (x >= start && x < end) {
                return i;
            }
        }
        return -1;
    }

    /**
     * The row ends at its nutritional line, or at the next row of day numbers when there is none.
     */
    private float rowBottom(Cell cell, List<Cell> cells, List<Line> lines, List<Float> columns) {
        float nextRowY = cells.stream()
                .map(Cell::y)
                .filter(y -> y > cell.y() + SAME_LINE_TOLERANCE)
                .min(Float::compare)
                .orElse(cell.y() + DEFAULT_ROW_HEIGHT);

        return lines.stream()
                .filter(line -> line.y() > cell.y() && line.y() < nextRowY)
                .filter(line -> IntStream.range(0, columns.size()).anyMatch(column ->
                        NUTRITION_LINE.matcher(toText(charactersInColumn(line, column, columns))).matches()))
                .map(Line::y)
                .max(Float::compare)
                .orElse(nextRowY);
    }

    private String buildMenu(Cell cell, float rowBottom, List<Line> lines, List<Float> columns) {
        List<List<TextPosition>> dishLines = new ArrayList<>();
        List<String> dishes = new ArrayList<>();
        Float previousY = null;
        float previousFontSize = 0;

        for (Line line : lines) {
            if (line.y() <= cell.y() + SAME_LINE_TOLERANCE || line.y() >= rowBottom - SAME_LINE_TOLERANCE) {
                continue;
            }
            List<TextPosition> characters = charactersInColumn(line, cell.column(), columns);
            String text = toText(characters);
            if (text.isBlank() || NUTRITION_LINE.matcher(text).matches()) {
                continue;
            }
            float fontSize = maxFontSize(characters);
            if (previousY != null && line.y() - previousY > Math.max(fontSize, previousFontSize) * DISH_GAP_FACTOR) {
                dishes.add(toDish(dishLines));
                dishLines = new ArrayList<>();
            }
            dishLines.add(characters);
            previousY = line.y();
            previousFontSize = fontSize;
        }
        if (!dishLines.isEmpty()) {
            dishes.add(toDish(dishLines));
        }
        return String.join("\n", dishes);
    }

    /**
     * The dish name uses the largest font of the block; the ingredients use a smaller one.
     */
    private String toDish(List<List<TextPosition>> dishLines) {
        float nameFontSize = (float) dishLines.stream().mapToDouble(this::maxFontSize).max().orElse(0);
        List<String> name = new ArrayList<>();
        List<String> description = new ArrayList<>();
        for (List<TextPosition> line : dishLines) {
            List<TextPosition> nameCharacters = new ArrayList<>();
            List<TextPosition> descriptionCharacters = new ArrayList<>();
            for (TextPosition character : line) {
                if (character.getFontSizeInPt() >= nameFontSize - FONT_SIZE_TOLERANCE) {
                    nameCharacters.add(character);
                } else {
                    descriptionCharacters.add(character);
                }
            }
            name.add(toText(nameCharacters));
            description.add(toText(descriptionCharacters));
        }
        String dishName = normalize(String.join(" ", name));
        String dishDescription = normalize(String.join(" ", description));
        return dishDescription.isEmpty() ? dishName : dishName + " (" + dishDescription + ")";
    }

    private float maxFontSize(List<TextPosition> characters) {
        return (float) characters.stream().mapToDouble(TextPosition::getFontSizeInPt).max().orElse(0);
    }

    private String toText(List<TextPosition> characters) {
        StringBuilder text = new StringBuilder();
        TextPosition previous = null;
        for (TextPosition character : characters) {
            if (previous != null) {
                float gap = character.getXDirAdj() - (previous.getXDirAdj() + previous.getWidthDirAdj());
                if (gap > character.getFontSizeInPt() * 0.25f) {
                    text.append(' ');
                }
            }
            text.append(character.getUnicode());
            previous = character;
        }
        return normalize(text.toString());
    }

    private String normalize(String text) {
        return text.replaceAll("\\s+", " ").trim();
    }

    private record Cell(int day, int column, float y) {}

    private record Word(String text, float x, float fontSize) {}

    private List<TextPosition> charactersInColumn(Line line, int column, List<Float> columns) {
        return line.characters().stream()
                .filter(character -> columnOf(character.getXDirAdj(), columns) == column)
                .toList();
    }

    private record Line(float y, List<TextPosition> characters) {

        List<Word> words() {
            List<Word> words = new ArrayList<>();
            StringBuilder text = new StringBuilder();
            float x = 0;
            float fontSize = 0;
            TextPosition previous = null;
            for (TextPosition character : characters) {
                boolean isSpace = character.getUnicode().isBlank();
                boolean gap = previous != null && character.getXDirAdj()
                        - (previous.getXDirAdj() + previous.getWidthDirAdj()) > character.getFontSizeInPt() * 0.25f;
                if ((isSpace || gap) && !text.isEmpty()) {
                    words.add(new Word(text.toString(), x, fontSize));
                    text.setLength(0);
                }
                if (!isSpace) {
                    if (text.isEmpty()) {
                        x = character.getXDirAdj();
                        fontSize = character.getFontSizeInPt();
                    }
                    text.append(character.getUnicode());
                }
                previous = character;
            }
            if (!text.isEmpty()) {
                words.add(new Word(text.toString(), x, fontSize));
            }
            return words;
        }
    }
}
