package protocol;

import java.io.File;
import java.util.Map;

/**
 *  
 * @author David Galvez
 */
public class Response413RequestEntityTooLarge extends HttpResponse {

		/**
		 * @param version
		 * @param header
		 * @param file
		 */
		public Response413RequestEntityTooLarge(String version, Map<String, String> header, File file) {
			super(version, Protocol.REQUEST_ENTITY_TOO_LARGE_CODE, Protocol.REQUEST_ENTITY_TOO_LARGE_TEXT, header, file);
		}

}
