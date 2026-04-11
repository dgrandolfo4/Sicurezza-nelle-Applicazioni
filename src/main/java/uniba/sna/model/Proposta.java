package uniba.sna.model;

import net.jcip.annotations.Immutable;

@Immutable
public class Proposta {
	private final int id;
    private final String nomeOriginale;
    private final String autoreEmail;
    private final String dataUpload;

    // Costruttore
    public Proposta(int id, String nomeOriginale, String autoreEmail, String dataUpload) {
    	this.id = id;
        this.nomeOriginale = nomeOriginale;
        this.autoreEmail = autoreEmail;
        this.dataUpload = dataUpload;
    }

    // Getter
    public int getId() { return id; }
    public String getNomeFile() { return nomeOriginale; }
    public String getAutoreEmail() { return autoreEmail; }
    public String getDataUpload() { return dataUpload; }
}
