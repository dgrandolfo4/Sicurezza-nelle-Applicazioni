package uniba.sna.model;

import net.jcip.annotations.Immutable;

@Immutable
public class Proposta {
    private final String nomeOriginale;
    private final String autoreEmail;
    private final String dataUpload;

    // Costruttore
    public Proposta( String nomeOriginale, String autoreEmail, String dataUpload) {
        this.nomeOriginale = nomeOriginale;
        this.autoreEmail = autoreEmail;
        this.dataUpload = dataUpload;
    }

    // Getter
    public String getNomeOriginale() { return nomeOriginale; }
    public String getAutoreEmail() { return autoreEmail; }
    public String getDataUpload() { return dataUpload; }
}
