package eu.planets_project.ifr.core.services.migration.genericwrapper1;

import eu.planets_project.ifr.core.services.migration.genericwrapper1.exceptions.MigrationException;
import eu.planets_project.ifr.core.services.migration.genericwrapper1.exceptions.NoSuchPathException;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * Create migration paths from an XML config file.
 * 
 * @author Asger Blekinge-Rasmussen
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class MigrationPaths {

//TODO: Should implement an interface to allow implement new versions to support new configuration file formats.
private Logger log = Logger.getLogger(MigrationPaths.class.getName());


    /**
     * <code>PathKey</code> is a utility class used for keys for storage and
     * look-up of migration paths.
     * 
     * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
     */
    private class PathKey {
        private final URI sourceFormat;
        private final URI destinationFormat;

        /**
         * Create a <code>PathKey</code> for a migration path with the
         * <code>sourceFormat URI</code> and the
         * <code>destinationFormat URI</code>.
         */
        public PathKey(URI sourceFormatURI, URI destinationFormatURI) {
            this.sourceFormat = sourceFormatURI;
            this.destinationFormat = destinationFormatURI;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime
                    * result
                    + ((destinationFormat == null) ? 0 : destinationFormat
                            .hashCode());
            result = prime * result
                    + ((sourceFormat == null) ? 0 : sourceFormat.hashCode());
            return result;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (!(obj instanceof PathKey))
                return false;
            PathKey other = (PathKey) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (destinationFormat == null) {
                if (other.destinationFormat != null)
                    return false;
            } else if (!destinationFormat.equals(other.destinationFormat))
                return false;
            if (sourceFormat == null) {
                if (other.sourceFormat != null)
                    return false;
            } else if (!sourceFormat.equals(other.sourceFormat))
                return false;
            return true;
        }

        private MigrationPaths getOuterType() {
            return MigrationPaths.this;
        }
    }

    private HashMap<PathKey, MigrationPath> migrationPaths;

    public MigrationPaths() {
        migrationPaths = new HashMap<PathKey, MigrationPath>();
    }

    /**
     * Get the <code>CliMigrationPath</code> for migration from the format
     * identified by the <code>sourceFormat URI</code> to the format identified
     * by the <code>destinationFormat URI</code>.
     * 
     * @param sourceFormat
     *            <code>URI</code> identifying the source format of the path to
     *            get.
     * @param destinationFormat
     *            <code>URI</code> identifying the destination format of the
     *            path to get.
     * @return the migration path found.
     * @throws MigrationException
     *             if no matching path was found.
     */
    public MigrationPath getMigrationPath(URI sourceFormat,
            URI destinationFormat) throws NoSuchPathException {

        final PathKey pathKey = new PathKey(sourceFormat, destinationFormat);
        final MigrationPath migrationPath = migrationPaths.get(pathKey);
        if (migrationPath == null) {
            throw new NoSuchPathException(
                    "No migration path found for source format URI=\""
                            + sourceFormat + "\" and destination format URI=\""
                            + destinationFormat + "\"");
        }
        return migrationPath;
    }

    // TODO: the getAsPlanetsPaths() functionality should probably go
    // somewhere else.
    /**
     * @return The migration paths as planets paths
     */
    // public MigrationPath[] getAsPlanetsPaths() {
    //
    // List<MigrationPath> planetspaths = new ArrayList<MigrationPath>();
    // for (CliMigrationPath mypath : migrationPaths) {
    // planetspaths.addAll(MigrationPath.constructPaths(mypath.getIn(),
    // mypath.getOut()));
    // }
    // return planetspaths.toArray(new MigrationPath[0]);
    //
    // }
    
    /**
     * Add <code>cliMigrationPath</code> to this collection of migration paths.
     * 
     * @param migrationPath
     *            the <code>CliMigrationPath</code> to add.
     * @return the previous <code>CliMigrationPath</code> instance stored for
     *         this migration path or <code>null</code> if there was no previous
     *         element.
     */
    public MigrationPath addMigrationPath(MigrationPath migrationPath) {
        final PathKey pathKey = new PathKey(migrationPath.getSourceFormat(),
                migrationPath.getDestinationFormat());
        return migrationPaths.put(pathKey, migrationPath);
    }


    public void addAll(List<MigrationPath> migrationpaths){
        for (MigrationPath path:migrationpaths){
            addMigrationPath(path);
        }
    }

    public List<eu.planets_project.services.datatypes.MigrationPath> getAsPlanetsPaths(){

        List<eu.planets_project.services.datatypes.MigrationPath> planetspaths
                = new ArrayList<eu.planets_project.services.datatypes.MigrationPath>();
        for (MigrationPath migrationPath : migrationPaths.values()) {
            planetspaths.add(migrationPath.getAsPlanetsPath());

        }
        return planetspaths;
    }
}
