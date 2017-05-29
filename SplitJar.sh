echo ==============================================================================
echo --- Cutting jt400.jar into jt400Small.jar that contains only needed components
echo ==============================================================================

echo The following command gets the directory in which this script is placed

script_dir=$(dirname $0)
echo script_dir=$(dirname $0)
echo $script_dir

echo -------------------------------------------------------------
echo The following command makes the application directory current

cd $script_dir
echo cd $script_dir

echo -----------------------------------------------------
echo The following command sets classpath to Java programs

echo export CLASSPATH="$CLASSPATH:./jt400.jar"
export CLASSPATH="$CLASSPATH:./jt400.jar"

echo -------------------------------------------
echo Store the new .jar to the current directory

echo java utilities.AS400ToolboxJarMaker -component AS400, JDBC, FTP, IntegratedFileSystem

java utilities.AS400ToolboxJarMaker -component AS400, JDBC, FTP, IntegratedFileSystem