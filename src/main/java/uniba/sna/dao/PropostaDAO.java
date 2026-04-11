package uniba.sna.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.jcip.annotations.NotThreadSafe;
import uniba.sna.utils.AppProperties;
import uniba.sna.model.File;
import uniba.sna.model.Proposta;

@NotThreadSafe
public class PropostaDAO extends DatabaseDAO {

    public boolean uploadProposta(String email, String fileName, String uniqueFileName) {
        boolean success = false;
        PreparedStatement psFile = null;

        try {
            if (connect()) {
            	String sqlFile = AppProperties.getQueryProperty("insertFileQuery");
                psFile = connessione.prepareStatement(sqlFile);
            	psFile.setString(1, fileName);
                psFile.setString(2, uniqueFileName);
                psFile.setInt(3, 2); // Category = 2 ==> Proposta Progettuale
                psFile.setString(4, email);
                
            	int fileRowsAffected = psFile.executeUpdate();
                if (fileRowsAffected > 0) {
                    success = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	try { 
                if (psFile != null) psFile.close(); 
            } catch (SQLException e) { 
                e.printStackTrace(); 
            }
            close();
        }
        
        return success;
    }
    
    public List<Proposta> getAllProposte() {
        List<Proposta> lista = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (connect()) {
                String sql = AppProperties.getQueryProperty("selectAllProposteQuery");
                ps = connessione.prepareStatement(sql);
                rs = ps.executeQuery();
                while (rs.next()) {
                    lista.add(new Proposta(
                    	rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4)
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
            close();
        }
        return lista;
    }
    
    public File getFileNamesById(int propostaId) {
        File file = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            if (connect()) {
                String sql = AppProperties.getQueryProperty("selectFileNameByIdQuery");
                ps = connessione.prepareStatement(sql);
                ps.setInt(1, propostaId);
                rs = ps.executeQuery();
                
                if (rs.next()) {
                	String originalName = rs.getString(1);
                    String uniqueName = rs.getString(2);
                	
                    file = new File(originalName, uniqueName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	try { if (ps != null) ps.close(); } catch (Exception e) {}
        	try { if (rs != null) rs.close(); } catch (Exception e) {}
        	
            close();
        }
        
        return file;
    }
}