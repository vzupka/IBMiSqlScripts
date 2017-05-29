echo ===========================================================================
echo --- Create executable Jar file Q_Menu.jar
echo ===========================================================================

echo The following command gets the directory in which the script is placed

script_dir=$(dirname $0)
echo script_dir=$(dirname $0)
echo $script_dir

echo -------------------------------------------------------------
echo The following command makes the application directory current

cd $script_dir
echo cd $script_dir

echo -------------------------------------------------------------------
echo The following command creates the Jar file in the current directory

echo jar cvfm  Q_Menu.jar  manifestMenu.txt  -C build/classes queries/Q_Menu.class  -C build/classes queries -C build/classes locales
jar cvfm  Q_Menu.jar  manifestMenu.txt  -C build/classes queries/Q_Menu.class  -C build/classes queries -C build/classes locales

echo -------------------------------------------
echo The following command executes the Jar file

echo java -jar Q_Menu.jar
java -jar Q_Menu.jar