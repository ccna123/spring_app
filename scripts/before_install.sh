DIR="/usr/bin/spring_app"
if [ -s "$DIR" ]; then
    echo "${DIR} exist"
else
    echo "Creating ${DIR} directory"
    sudo mkdir ${DIR}
fi