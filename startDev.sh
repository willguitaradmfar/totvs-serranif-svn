export svnURL=$1
export svnUserName=$2		
export svnPassword=$3
export svnProtocol=$4

echo 'USERNAME : '$svnUserName

echo 'URL : '$svnURL

mvn clean install

java -DignorarLinhasBrancas=true -DstartRevision=2440 -DlastRevision=2443 -jar target/SVNLogConvertor-0.0.1-SNAPSHOT.jar
