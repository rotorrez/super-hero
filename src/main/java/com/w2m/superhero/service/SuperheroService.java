find . -name "*.jar" -exec sh -c 'jar tf {} | grep -q QuarkusRestPathTemplate && echo {}' \;
