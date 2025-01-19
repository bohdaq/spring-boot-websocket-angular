mvn clean install

cd frontend

ng build --configuration=development

cd ..

docker-compose up --build
