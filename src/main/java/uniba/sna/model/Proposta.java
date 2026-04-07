package uniba.sna.model;

public class Proposta {
    private String nomeOriginale;
    private String autoreEmail;
    private String dataUpload;

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
