package batallapokemon.modelo;

public enum Tipo {
    NORMAL("Normal"), FUEGO("Fuego"), AGUA("Agua"), PLANTA("Planta"),
    ELECTRICO("Electrico"), HIELO("Hielo"), LUCHA("Lucha"), VENENO("Veneno"),
    TIERRA("Tierra"), VOLADOR("Volador"), PSIQUICO("Psiquico"), BICHO("Bicho"),
    ROCA("Roca"), FANTASMA("Fantasma"), DRAGON("Dragon"), SINIESTRO("Siniestro"),
    ACERO("Acero"), HADA("Hada");

    private final String etiqueta;

    Tipo(String etiqueta) { this.etiqueta = etiqueta; }

    public String getEtiqueta() { return etiqueta; }

    public static Tipo desdeApi(String nombreIngles) {
        if (nombreIngles == null) return NORMAL;
        switch (nombreIngles.toLowerCase()) {
            case "normal":   return NORMAL;
            case "fire":     return FUEGO;
            case "water":    return AGUA;
            case "grass":    return PLANTA;
            case "electric": return ELECTRICO;
            case "ice":      return HIELO;
            case "fighting": return LUCHA;
            case "poison":   return VENENO;
            case "ground":   return TIERRA;
            case "flying":   return VOLADOR;
            case "psychic":  return PSIQUICO;
            case "bug":      return BICHO;
            case "rock":     return ROCA;
            case "ghost":    return FANTASMA;
            case "dragon":   return DRAGON;
            case "dark":     return SINIESTRO;
            case "steel":    return ACERO;
            case "fairy":    return HADA;
            default:         return NORMAL;
        }
    }

    @Override
    public String toString() { return etiqueta; }
}
