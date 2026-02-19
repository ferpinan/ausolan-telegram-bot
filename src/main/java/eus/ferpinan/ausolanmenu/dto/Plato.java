package eus.ferpinan.ausolanmenu.dto;

import lombok.Data;

@Data
public class Plato {
    private String nombre;
    private Dieta dieta;
    private ValorNutricionalPlato valorNutricionalPlato;
    private int ordenPlato;
    private int diaSemana;
    private int rotacion;
    private String articuloPk;
    private String familiaTecnicaPk;
}
