#!/bin/bash
# ==============================================================================
# SCRIPT DE POVOAMENTO DE DADOS (SEED) - VERSÃO FINAL E COMPATÍVEL
# ==============================================================================

# --- Configuração ---
AUTH="admin:123456"
BASE_URL="http://localhost:8080/api"
HEADER="Content-Type: application/json"

echo "======================================================"
echo "🚌 INICIANDO POVOAMENTO DO BANCO DE DADOS SGO..."
echo "======================================================"

# --- 1. PRODUTOS (Total: 200) ---
echo "📦 Populando Produtos..."

# --- 1.1 Fluidos e Óleos (7 itens) ---
echo "   -> Criando Óleos de Motor e Fluidos de Câmbio..."
# Óleos de Motor (4)
curl -s -X POST -u $AUTH -H "$HEADER" -d '{"nome": "Óleo Motor Diesel 15W-40", "marca": "Mobil Delvac", "unidadeMedida": "LITRO", "codigoInterno": "PROD-0001", "precoInicial": 45.50, "estoqueMinimo": 50, "categoria": "FLUIDO"}' "$BASE_URL/produtos"
curl -s -X POST -u $AUTH -H "$HEADER" -d '{"nome": "Óleo Motor Diesel 10W-30", "marca": "Shell Rimula", "unidadeMedida": "LITRO", "codigoInterno": "PROD-0002", "precoInicial": 52.00, "estoqueMinimo": 40, "categoria": "FLUIDO"}' "$BASE_URL/produtos"
curl -s -X POST -u $AUTH -H "$HEADER" -d '{"nome": "Óleo Motor Sintético 5W-30", "marca": "Petrobras Lubrax", "unidadeMedida": "LITRO", "codigoInterno": "PROD-0003", "precoInicial": 65.75, "estoqueMinimo": 20, "categoria": "FLUIDO"}' "$BASE_URL/produtos"
curl -s -X POST -u $AUTH -H "$HEADER" -d '{"nome": "Óleo para Motores Scania", "marca": "Ipiranga Brutus", "unidadeMedida": "LITRO", "codigoInterno": "PROD-0004", "precoInicial": 48.00, "estoqueMinimo": 30, "categoria": "FLUIDO"}' "$BASE_URL/produtos"
# Fluidos de Câmbio (3)
curl -s -X POST -u $AUTH -H "$HEADER" -d '{"nome": "Fluido de Transmissão ATF Dexron III", "marca": "Tutela", "unidadeMedida": "LITRO", "codigoInterno": "PROD-0005", "precoInicial": 55.00, "estoqueMinimo": 25, "categoria": "FLUIDO"}' "$BASE_URL/produtos"
curl -s -X POST -u $AUTH -H "$HEADER" -d '{"nome": "Fluido de Câmbio Automático", "marca": "Valvoline", "unidadeMedida": "LITRO", "codigoInterno": "PROD-0006", "precoInicial": 60.00, "estoqueMinimo": 20, "categoria": "FLUIDO"}' "$BASE_URL/produtos"
curl -s -X POST -u $AUTH -H "$HEADER" -d '{"nome": "Óleo Engrenagem 80W-90", "marca": "ZF Ecofluid", "unidadeMedida": "LITRO", "codigoInterno": "PROD-0007", "precoInicial": 75.00, "estoqueMinimo": 15, "categoria": "FLUIDO"}' "$BASE_URL/produtos"

# --- 1.2 Ferramentas (30 itens) ---
echo "   -> Criando Ferramentas..."
tools=(
    "Alicate de Pressão 10\"|Tramontina|28.50"
    "Martelo de Borracha 500g|Gedore|45.00"
    "Jogo Chave de Fenda/Phillips 6pçs|Belzer|89.90"
    "Chave Combinada 13mm|Tramontina PRO|15.20"
    "Chave Combinada 17mm|Tramontina PRO|19.80"
    "Chave Combinada 19mm|Tramontina PRO|24.50"
    "Torquímetro de Estalo 1/2\"|Sata|350.00"
    "Macaco Hidráulico Jacaré 2T|Bovenau|450.00"
    "Cavalete de Segurança 3T (Par)|Potente Brasil|180.00"
    "Multímetro Digital|Minipa|95.00"
    "Lâmpada de Ponto|Plana|120.00"
    "Compressor de Molas de Válvula|Raven|210.00"
    "Saca Polia 3 Garras|Gedore|150.00"
    "Extrator de Rolamento|SKF|300.00"
    "Pistola de Pintura HVLP|Wimpel|180.00"
    "Desengraxante Industrial 5L|H-7|90.00"
    "Funil para Óleo|Plastcor|8.00"
    "Bandeja Coletora de Óleo 15L|Lubefer|60.00"
    "Jogo de Soquetes Estriados 1/2\" 22pçs|Gedore|450.00"
    "Cabo de Força 1/2\"|Tramontina PRO|75.00"
    "Manômetro de Pressão de Pneus|Staub|55.00"
    "Calibrador de Pneus Eletrônico|Schulz|250.00"
    "Chave de Roda em Cruz|Tramontina|40.00"
    "Espátula para Montagem de Pneus|Gedore|35.00"
    "Escova de Aço para Limpeza|Vonder|12.00"
    "Óculos de Proteção|3M|15.00"
    "Luva de Malha Pigmentada (Par)|Danny|5.00"
    "Protetor Auricular Plug|3M|3.00"
    "Lanterna de LED Recarregável|Foxlux|65.00"
    "Carrinho de Ferramentas Fechado|Marccon|850.00"
)
counter=8
for item in "${tools[@]}"; do
    IFS='|' read -r name brand price <<< "$item"
    CODE_INT=$(printf "PROD-%04d" $counter)
    curl -s -X POST -u $AUTH -H "$HEADER" -d "{\"nome\": \"$name\", \"marca\": \"$brand\", \"unidadeMedida\": \"UNIDADE\", \"codigoInterno\": \"$CODE_INT\", \"precoInicial\": $price, \"estoqueMinimo\": 2, \"categoria\": \"FERRAMENTA\"}" "$BASE_URL/produtos"
    ((counter++))
done

# --- 1.3 Peças Genéricas e Outros (163 itens) ---
echo "   -> Criando Peças Genéricas e Outros Itens..."
generic_items=(
    "Filtro de Ar do Motor|Bosch|65.00|PECA_GENERICA|UNIDADE"
    "Pastilha de Freio Dianteira|Fras-le|180.00|PECA_GENERICA|PAR"
    "Lona de Freio Traseira|Cobreq|250.00|PECA_GENERICA|JOGO"
    "Filtro de Combustível|Tecfil|45.00|PECA_GENERICA|UNIDADE"
    "Rolamento de Roda Dianteiro|SKF|120.00|PECA_GENERICA|UNIDADE"
    "Lâmpada Farol H4 24V|Osram|25.00|PECA_GENERICA|UNIDADE"
    "Palheta Limpador 28\"|Bosch|40.00|PECA_GENERICA|UNIDADE"
    "Bateria 150Ah|Moura|850.00|PECA_GENERICA|UNIDADE"
    "Correia do Alternador|Continental|55.00|PECA_GENERICA|UNIDADE"
    "Amortecedor Dianteiro|Monroe|350.00|PECA_GENERICA|UNIDADE"
    "Terminal de Direção|Nakata|95.00|PECA_GENERICA|UNIDADE"
    "Cruzeta do Cardan|Spicer|150.00|PECA_GENERICA|UNIDADE"
    "Cilindro Mestre de Freio|Varga|450.00|PECA_GENERICA|UNIDADE"
    "Estopa para Limpeza|Generico|15.00|OUTRO|QUILOGRAMA"
    "Abraçadeira de Nylon 30cm|Generico|0.50|OUTRO|UNIDADE"
    "Fita Isolante 10m|3M|5.00|OUTRO|UNIDADE"
)
for i in $(seq 1 163); do
    item_data=${generic_items[$((i % ${#generic_items[@]}))]}
    IFS='|' read -r name brand price category unit <<< "$item_data"
    CODE_INT=$(printf "PROD-%04d" $counter)
    # Adiciona um sufixo para diferenciar os itens
    FINAL_NAME="$name #$((i/10))"
    curl -s -X POST -u $AUTH -H "$HEADER" -d "{\"nome\": \"$FINAL_NAME\", \"marca\": \"$brand\", \"unidadeMedida\": \"$unit\", \"codigoInterno\": \"$CODE_INT\", \"precoInicial\": $price, \"estoqueMinimo\": 10, \"categoria\": \"ITEM_GENERICO\"}" "$BASE_URL/produtos"
    ((counter++))
done
echo "   -> Produtos populados com sucesso!"

# --- 2. COMPONENTES ---

# --- 2.1 Motores (22 itens) ---
echo "⚙️  Populando Motores..."
for i in $(seq 1 22); do
    STATUS="NOVO"
    if [ $i -gt 18 ]; then STATUS="DISPONIVEL"; fi
    MARCA="Mercedes-Benz"
    MODELO="OM-501 LA"
    if [ $((i % 3)) -eq 0 ]; then MARCA="Scania"; MODELO="DC13"; fi
    if [ $((i % 4)) -eq 0 ]; then MARCA="Volvo"; MODELO="D13K"; fi
    ANO=$((2020 + i % 5))
    curl -s -X POST -u $AUTH -H "$HEADER" -d "{\"tipo\":\"DIESEL\",\"capacidadeOleo\":30.0,\"potencia\":420,\"marca\":\"$MARCA\",\"modelo\":\"$MODELO\",\"anoFabricacao\":$ANO,\"codigoFabricacao\":\"MOT-C-$(printf "%03d" $i)\",\"numeroSerie\":\"MOT-S-$(printf "%03d" $i)\",\"dataCompra\":\"$ANO-05-10\",\"periodoGarantiaMeses\":24,\"tipoOleo\":\"Óleo Motor Diesel 15W-40\"}" "$BASE_URL/motores" > /dev/null
done
echo "   -> Motores populados com sucesso!"

# --- 2.2 Câmbios (23 itens) ---
echo "🔩 Populando Câmbios..."
for i in $(seq 1 23); do
    STATUS="NOVO"
    if [ $i -gt 19 ]; then STATUS="DISPONIVEL"; fi
    MARCA="ZF"
    MODELO="AS Tronic"
    TIPO="AUTOMATIZADO"
    if [ $((i % 2)) -eq 0 ]; then MARCA="Allison"; MODELO="Torqmatic"; TIPO="AUTOMATICO"; fi
    if [ $((i % 5)) -eq 0 ]; then MARCA="Voith"; MODELO="DIWA.6"; TIPO="AUTOMATICO"; fi
    ANO=$((2021 + i % 4))
    curl -s -X POST -u $AUTH -H "$HEADER" -d "{\"tipo\":\"$TIPO\",\"numeroMarchas\":12,\"marca\":\"$MARCA\",\"modelo\":\"$MODELO\",\"anoFabricacao\":$ANO,\"codigoFabricacao\":\"CAM-C-$(printf "%03d" $i)\",\"numeroSerie\":\"CAM-S-$(printf "%03d" $i)\",\"tipoFluido\":\"Fluido de Transmissão ATF Dexron III\",\"capacidadeFluido\":20.0,\"dataCompra\":\"$ANO-03-15\",\"periodoGarantiaMeses\":24}" "$BASE_URL/cambios" > /dev/null
done
echo "   -> Câmbios populados com sucesso!"



# --- 3. ÔNIBUS (20 itens) ---
echo "🚌 Populando Ônibus..."
for i in $(seq 1 20); do
    MARCA="Marcopolo"
    MODELO="Torino S"
    if [ $((i % 3)) -eq 0 ]; then MARCA="Caio"; MODELO="Apache Vip V"; fi
    if [ $((i % 5)) -eq 0 ]; then MARCA="Busscar"; MODELO="Urbanuss Pluss"; fi
    ANO=$((2022 + i % 4))
    
    # Lógica universal para gerar letras da placa
    PLACA_LETRA=$(openssl rand -base64 6 | tr -dc 'A-Z' | head -c3)
    PLACA_NUM=$(printf "%04d" $i)
    
    # <<< MELHORIA: JSON formatado e com um echo para debugging >>>
    JSON_PAYLOAD=$(cat <<EOF
{
    "chassi": "9BW154942$(printf "%08d" $i)",
    "placa": "$PLACA_LETRA-$PLACA_NUM",
    "modelo": "$MODELO",
    "marca": "$MARCA",
    "codigoFabricacao": "ONIBUS-C-$(printf "%03d" $i)",
    "capacidade": 44,
    "anoFabricacao": $ANO,
    "numeroFrota": "FROTA-$(printf "%04d" $i)",
    "dataCompra": "$ANO-02-01"
}
EOF
)
    
    # Imprime o payload que será enviado para facilitar a depuração
    echo "Enviando Ônibus $i: $PLACA_LETRA-$PLACA_NUM"
    
    # Envia a requisição
    curl -s -X POST -u $AUTH -H "$HEADER" -d "$JSON_PAYLOAD" "$BASE_URL/onibus" > /dev/null
done
echo "   -> Ônibus populados com sucesso!"

echo ""
echo "✅ Povoamento do banco de dados concluído com sucesso!"
echo "======================================================"

