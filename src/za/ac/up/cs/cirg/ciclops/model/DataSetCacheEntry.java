/*
 * Created on Apr 8, 2004
 */
package za.ac.up.cs.cirg.ciclops.model;

import java.io.Serializable;

/**
 * @author espeer
 */
public class DataSetCacheEntry implements Serializable {
        
        public DataSetCacheEntry(int id, int version) {
            this.id = id;
            this.version = version;
        }
        
        /**
         * @return Returns the id.
         */
        public int getId() {
            return id;
        }        
        
		/**
		 * @return Returns the version.
		 */
		public int getVersion() {
			return version;
		}
        private int id;
        private int version;
}
