package uniba.sna.model;

import net.jcip.annotations.Immutable;

@Immutable
public class File {
    private final String nomeOriginale;
    private final String nomeUnivoco;

    public File(String nomeOriginale, String nomeUnivoco) {
        this.nomeOriginale = nomeOriginale;
        this.nomeUnivoco = nomeUnivoco;
    }

    public String getNomeOriginale() {
        return nomeOriginale;
    }

    public String getNomeUnivoco() {
        return nomeUnivoco;
    }
}
