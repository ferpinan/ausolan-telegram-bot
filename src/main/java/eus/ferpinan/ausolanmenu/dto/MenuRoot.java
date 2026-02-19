package eus.ferpinan.ausolanmenu.dto;

import lombok.Data;
import java.util.List;

@Data
public class MenuRoot {
    private String menuPk;
    private String fechaInicioMenu;
    private String fechaFinMenu;
    private int rotacionActiva;
    private int maxRotacion;
    private List<Plato> platos;
}
