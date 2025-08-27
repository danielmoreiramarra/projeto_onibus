# Definir as credenciais de autenticação
AUTH="admin:123456"

# Produtos (3 ATIVOS, 1 INATIVO, 3 FLUIDOS, 1 FERRAMENTA, 1 OUTRO)
curl -X POST -u $AUTH -H "Content-Type: application/json" -d '{ "nome": "Filtro de Ar", "marca": "Bosch", "unidadeMedida": "UNIDADE", "codigoInterno": "PROD-0001", "precoUnitario": 50.00, "estoqueMinimo": 10, "categoria": "PECA_GENERICA", "status": "ATIVO" }' http://localhost:8080/api/produtos
curl -X POST -u $AUTH -H "Content-Type: application/json" -d '{ "nome": "Pastilha de Freio", "marca": "Fras-le", "unidadeMedida": "PAR", "codigoInterno": "PROD-0002", "precoUnitario": 120.50, "estoqueMinimo": 5, "categoria": "PECA_GENERICA", "status": "ATIVO" }' http://localhost:8080/api/produtos
curl -X POST -u $AUTH -H "Content-Type: application/json" -d '{ "nome": "Correia Dentada", "marca": "Continental", "unidadeMedida": "UNIDADE", "codigoInterno": "PROD-0003", "precoUnitario": 85.00, "estoqueMinimo": 3, "categoria": "PECA_GENERICA", "status": "INATIVO" }' http://localhost:8080/api/produtos
curl -X POST -u $AUTH -H "Content-Type: application/json" -d '{ "nome": "Óleo de Motor 15W40", "marca": "Petrobras", "unidadeMedida": "LITRO", "codigoInterno": "PROD-0004", "precoUnitario": 35.00, "estoqueMinimo": 50, "categoria": "FLUIDO", "status": "ATIVO" }' http://localhost:8080/api/produtos
curl -X POST -u $AUTH -H "Content-Type: application/json" -d '{ "nome": "Chave de Fenda", "marca": "Tramontina", "unidadeMedida": "UNIDADE", "codigoInterno": "PROD-0005", "precoUnitario": 15.00, "estoqueMinimo": 2, "categoria": "FERRAMENTA", "status": "ATIVO" }' http://localhost:8080/api/produtos
curl -X POST -u $AUTH -H "Content-Type: application/json" -d '{ "nome": "Parafuso M8", "marca": "Gerdau", "unidadeMedida": "UNIDADE", "codigoInterno": "PROD-0006", "precoUnitario": 1.25, "estoqueMinimo": 100, "categoria": "OUTRO", "status": "ATIVO" }' http://localhost:8080/api/produtos
curl -X POST -u $AUTH -H "Content-Type: application/json" -d '{ "nome": "Aditivo de Combustível", "marca": "Bardahl", "unidadeMedida": "FRASCO", "codigoInterno": "PROD-0007", "precoUnitario": 25.00, "estoqueMinimo": 10, "categoria": "FLUIDO", "status": "ATIVO" }' http://localhost:8080/api/produtos
curl -X POST -u $AUTH -H "Content-Type: application/json" -d '{ "nome": "Graxa Lubrificante", "marca": "Shell", "unidadeMedida": "QUILOGRAMA", "codigoInterno": "PROD-0008", "precoUnitario": 45.00, "estoqueMinimo": 8, "categoria": "FLUIDO", "status": "ATIVO" }' http://localhost:8080/api/produtos
curl -X POST -u $AUTH -H "Content-Type: application/json" -d '{ "nome": "Arruela", "marca": "Gerdau", "unidadeMedida": "UNIDADE", "codigoInterno": "PROD-0009", "precoUnitario": 0.50, "estoqueMinimo": 200, "categoria": "OUTRO", "status": "ATIVO" }' http://localhost:8080/api/produtos

# Motores (9 itens)
for i in $(seq 1 9); do
  STATUS="NOVO"
  if [ $i -gt 7 ]; then STATUS="EM_MANUTENCAO"; fi
  curl -X POST -u $AUTH -H "Content-Type: application/json" -d "{\"tipo\":\"DIESEL\",\"quantidadeOleo\":20.0,\"potencia\":300,\"marca\":\"Mercedes\",\"modelo\":\"OM457LA-$i\",\"anoFabricacao\":2020,\"codigoFabricacao\":\"OM457LA-C$i\",\"numeroSerie\":\"OM457LA-S$i\",\"dataCompra\":\"2020-01-01\",\"periodoGarantiaMeses\":24,\"status\":\"$STATUS\"}" http://localhost:8080/api/motores
done

# Câmbios (10 itens)
for i in $(seq 1 10); do
  STATUS="NOVO"
  if [ $i -gt 8 ]; then STATUS="DISPONIVEL"; fi
  curl -X POST -u $AUTH -H "Content-Type: application/json" -d "{\"tipo\":\"AUTOMATICO\",\"numeroMarchas\":6,\"marca\":\"ZF\",\"modelo\":\"ZF-ECOMAT-$i\",\"anoFabricacao\":2021,\"codigoFabricacao\":\"ZF-C$i\",\"numeroSerie\":\"ZF-S$i\",\"tipoFluido\":\"Fluido ATF\",\"quantidadeFluido\":15.0,\"dataCompra\":\"2021-02-01\",\"periodoGarantiaMeses\":24,\"status\":\"$STATUS\"}" http://localhost:8080/api/cambios
done

# Pneus (44 itens)
for i in $(seq 1 44); do
  STATUS="NOVO"
  if [ $i -gt 35 ]; then STATUS="DISPONIVEL"; fi
  if [ $i -gt 40 ]; then STATUS="EM_MANUTENCAO"; fi
  if [ $i -gt 42 ]; then STATUS="REFORMADO"; fi
  curl -X POST -u $AUTH -H "Content-Type: application/json" -d "{\"marca\":\"Michelin\",\"medida\":\"295/80R22.5\",\"codigoFabricacao\":\"PNEU-C$i\",\"numeroSerie\":\"PNEU-S$i\",\"anoFabricacao\":2022,\"dataCompra\":\"2022-03-01\",\"periodoGarantiaMeses\":36,\"status\":\"$STATUS\"}" http://localhost:8080/api/pneus
done

# Ônibus (8 itens)
for i in $(seq 1 8); do
  STATUS="NOVO"
  if [ $i -gt 6 ]; then STATUS="DISPONIVEL"; fi
  if [ $i -gt 7 ]; then STATUS="EM_MANUTENCAO"; fi
  curl -X POST -u $AUTH -H "Content-Type: application/json" -d "{\"chassi\":\"9BR154942$i\",\"placa\":\"ABC-123$i\",\"modelo\":\"Urbanus\",\"marca\":\"Marcopolo\",\"codigoFabricacao\":\"ONIBUS-C$i\",\"capacidade\":45,\"anoFabricacao\":2023,\"numeroFrota\":\"FROTA-10$i\",\"status\":\"$STATUS\"}" http://localhost:8080/api/onibus
done