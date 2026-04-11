package uniba.sna.utils;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class FileHelper {
    
	public static String escapeHtml(String input) {
        if (input == null) {
            return null;
        }

        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#x27;");
    }
}