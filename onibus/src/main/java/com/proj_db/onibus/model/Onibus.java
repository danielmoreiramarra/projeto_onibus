package com.proj_db.onibus.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.proj_db.onibus.model.HistoricoComponente.TipoEvento;
import com.proj_db.onibus.model.Pneu.PosicaoPneu;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "onibus")
public class Onibus {

    // <<< NOVO: Constantes para os limites de manutenção/reforma do ônibus
    private static final long DIAS_LIMITE_MANUTENCAO = 120; // 4 meses
    private static final long DIAS_LIMITE_REFORMA = 365;   // 1 ano

    // --- ATRIBUTOS ---
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ano_fabricacao", nullable = false)
    @NotNull
    private Integer anoFabricacao;

    @Column(name = "capacidade", nullable = false)
    @NotNull
    private Integer capacidade;

    @Column(name = "chassi", nullable = false, length = 100, unique = true)
    @NotBlank
    private String chassi;

    @Column(name = "codigo_fabricacao", unique = true, nullable = false, length = 50)
    @NotBlank
    private String codigoFabricacao;

    @Column(name = "data_compra", nullable = false)
    @NotNull
    private LocalDate dataCompra;

    @Column(name = "quilometragem", nullable = false)
    @NotNull
    @PositiveOrZero
    private Double quilometragem = 0.0;

    @Column(name = "marca", nullable = false, length = 100)
    @NotBlank
    private String marca;

    @Column(name = "modelo", nullable = false, length = 50)
    @NotBlank
    private String modelo;

    @Column(name = "numero_frota", unique = true, nullable = false, length = 20)
    @NotBlank
    private String numeroFrota;

    @Column(name = "placa", nullable = false, length = 100, unique = true)
    @NotBlank
    private String placa;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @NotNull
    private StatusOnibus status = StatusOnibus.NOVO;
    

    // --- RELACIONAMENTOS E HISTÓRICOS ---
    @OneToOne(cascade = CascadeType.ALL)
    @JsonManagedReference
    @JoinColumn(name = "motor_id", referencedColumnName = "id")
    private Motor motor;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonManagedReference
    @JoinColumn(name = "cambio_id", referencedColumnName = "id")
    private Cambio cambio;

    @JsonManagedReference
    @OneToMany(mappedBy = "onibus", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Pneu> pneus = new ArrayList<>();
    
    @JsonManagedReference
    @OneToMany(mappedBy = "onibus", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoricoComponente> historicoComponentes = new ArrayList<>();
    
    @ElementCollection
    private List<LocalDate> historicoEnvioManutencao = new ArrayList<>();
    @ElementCollection
    private List<LocalDate> historicoEnvioReforma = new ArrayList<>();
    @ElementCollection
    private List<LocalDate> historicoRetornoManutencao = new ArrayList<>();
    @ElementCollection
    private List<LocalDate> historicoRetornoReforma = new ArrayList<>();
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "onibus_historico_operacional", joinColumns = @JoinColumn(name = "onibus_id"))
    private List<RegistroOperacional> historicoOperacional = new ArrayList<>();

    
    // --- CLASSE EMBUTIDA E ENUMS ---
    @Embeddable // Anotação que diz ao JPA que esta classe pode ser embutida em outras
    @Data // Lombok para getters/setters
    public static class RegistroOperacional {

        @Column(name = "data_registro", nullable = false)
        private LocalDate data;

        @Column(name = "quilometragem_no_registro", nullable = false)
        private Double quilometragem;

        @Enumerated(EnumType.STRING)
        @Column(name = "tipo_registro", nullable = false)
        private TipoRegistro tipoRegistro;

        public enum TipoRegistro {
            INICIO_OPERACAO,
            FIM_OPERACAO
        }
    }

    public enum StatusOnibus { NOVO, DISPONIVEL, EM_OPERACAO, EM_MANUTENCAO, EM_REFORMA, APOSENTADO, VENDIDO }

    
    // --- MÉTODOS DE GERENCIAMENTO DE COMPONENTES ---

    // <<< NOVO: Método auxiliar para centralizar a validação de status
    private void checarSePodeAlterarComponentes() {
        if (this.status != StatusOnibus.NOVO && this.status != StatusOnibus.DISPONIVEL) {
            throw new IllegalStateException("Componentes só podem ser alterados se o ônibus estiver com status NOVO ou DISPONÍVEL.");
        }
    }

    public void instalarMotor(Motor novoMotor) {
        checarSePodeAlterarComponentes();
        if (this.motor != null) throw new IllegalStateException("Ônibus já possui um motor. Remova o atual primeiro.");
        if (novoMotor.getStatus() != Motor.StatusMotor.NOVO && novoMotor.getStatus() != Motor.StatusMotor.DISPONIVEL) {
            throw new IllegalStateException("Motor precisa estar NOVO ou DISPONIVEL.");
        }
        this.motor = novoMotor;
        novoMotor.instalar(this);
        this.historicoComponentes.add(new HistoricoComponente(this, novoMotor, TipoEvento.INSTALADO, null));
    }

    public void instalarCambio(Cambio novoCambio) {
        checarSePodeAlterarComponentes();
        if (this.cambio != null) throw new IllegalStateException("Ônibus já possui um câmbio. Remova o atual primeiro.");
        if (novoCambio.getStatus() != Cambio.StatusCambio.NOVO && novoCambio.getStatus() != Cambio.StatusCambio.DISPONIVEL) {
            throw new IllegalStateException("Câmbio precisa estar NOVO ou DISPONIVEL.");
        }
        this.cambio = novoCambio;
        novoCambio.instalar(this);
        this.historicoComponentes.add(new HistoricoComponente(this, novoCambio, TipoEvento.INSTALADO, null));
    }

    public void instalarPneu(Pneu novoPneu, PosicaoPneu posicao) {
        checarSePodeAlterarComponentes();
        if (pneus.stream().anyMatch(p -> p.getPosicao() == posicao)) {
            throw new IllegalStateException("Posição " + posicao + " já está ocupada.");
        }
        if (novoPneu.getStatus() != Pneu.StatusPneu.NOVO && novoPneu.getStatus() != Pneu.StatusPneu.DISPONIVEL) {
            throw new IllegalStateException("Pneu precisa estar NOVO ou DISPONIVEL.");
        }
        this.pneus.add(novoPneu);
        novoPneu.instalar(this, posicao);
        this.historicoComponentes.add(new HistoricoComponente(this, novoPneu, TipoEvento.INSTALADO, posicao));
    }

    public void removerMotor() {
        checarSePodeAlterarComponentes();
        if (this.motor != null) {
            Motor motorRemovido = this.motor;
            this.motor = null;
            motorRemovido.remover(this);
            this.historicoComponentes.add(new HistoricoComponente(this, motorRemovido, TipoEvento.REMOVIDO, null));
        }
    }
    
    public void removerCambio() {
        checarSePodeAlterarComponentes();
        if (this.cambio != null) {
            Cambio cambioRemovido = this.cambio;
            this.cambio = null;
            cambioRemovido.remover(this);
            this.historicoComponentes.add(new HistoricoComponente(this, cambioRemovido, TipoEvento.REMOVIDO, null));
        }
    }

    public void removerPneu(PosicaoPneu posicao) {
        checarSePodeAlterarComponentes();
        Pneu pneuParaRemover = this.pneus.stream()
            .filter(p -> p.getPosicao() == posicao).findFirst().orElse(null);

        if (pneuParaRemover != null) {
            this.pneus.remove(pneuParaRemover);
            pneuParaRemover.remover(this);
            this.historicoComponentes.add(new HistoricoComponente(this, pneuParaRemover, TipoEvento.REMOVIDO, posicao));
        }
    }
    
    // --- MÉTODOS DE LÓGICA DE NEGÓCIO E CICLO DE VIDA ---
    public void registrarViagem(Double quilometrosPercorridos) {
        if (this.status != StatusOnibus.EM_OPERACAO) {
            throw new IllegalStateException("Ônibus precisa estar EM_OPERACAO para registrar uma viagem.");
        }
        if (quilometrosPercorridos < 0) {
            throw new IllegalArgumentException("Quilometragem não pode ser negativa.");
        }

        // Atualiza o KM do ônibus
        this.quilometragem += quilometrosPercorridos;

        // Atualiza o KM de cada pneu
        for (Pneu pneu : this.pneus) {
            pneu.setKmRodados(pneu.getKmRodados() + quilometrosPercorridos);
        }
    }


    // --- MÉTODOS DE CICLO DE VIDA ---

    public void colocarEmOperacao() {
        if (this.status == StatusOnibus.NOVO || this.status == StatusOnibus.DISPONIVEL) {
            if (this.motor == null || this.cambio == null || this.pneus.size() != 6) {
                throw new IllegalStateException("Ônibus não pode ser colocado em operação. Verifique se todos os 6 pneus, motor e câmbio estão instalados.");
            }
            this.status = StatusOnibus.EM_OPERACAO;

            // <<< LÓGICA CORRETA PARA ADICIONAR AO HISTÓRICO >>>
            RegistroOperacional registro = new RegistroOperacional();
            registro.setData(LocalDate.now());
            registro.setQuilometragem(this.quilometragem);
            registro.setTipoRegistro(RegistroOperacional.TipoRegistro.INICIO_OPERACAO);
            this.historicoOperacional.add(registro);
        }
    }

    public void retirarDeOperacao() {
        if (this.status == StatusOnibus.EM_OPERACAO) {
            this.status = StatusOnibus.DISPONIVEL;

            // <<< LÓGICA CORRETA PARA ADICIONAR AO HISTÓRICO >>>
            RegistroOperacional registro = new RegistroOperacional();
            registro.setData(LocalDate.now());
            registro.setQuilometragem(this.quilometragem);
            registro.setTipoRegistro(RegistroOperacional.TipoRegistro.FIM_OPERACAO);
            this.historicoOperacional.add(registro);
        }
    }

    public void enviarParaManutencao() {
        if (this.status == StatusOnibus.EM_OPERACAO) retirarDeOperacao();
        
        if (this.status == StatusOnibus.DISPONIVEL || this.status == StatusOnibus.NOVO) {
            this.status = StatusOnibus.EM_MANUTENCAO;
            this.historicoEnvioManutencao.add(LocalDate.now());
        }
    }
    
    public void retornarDaManutencao() {
        if (this.status == StatusOnibus.EM_MANUTENCAO) {
            this.status = StatusOnibus.DISPONIVEL;
            this.historicoRetornoManutencao.add(LocalDate.now());
        }
    }
    
    // <<< LÓGICA REFINADA: Reforma do ônibus não remove componentes automaticamente.
    public void enviarParaReforma() {
        if (this.status == StatusOnibus.EM_OPERACAO) retirarDeOperacao();

        if (this.status == StatusOnibus.DISPONIVEL || this.status == StatusOnibus.NOVO) {
            this.status = StatusOnibus.EM_REFORMA;
            this.historicoEnvioReforma.add(LocalDate.now());
        }
    }
    
    public void retornarDaReforma() {
        if (this.status == StatusOnibus.EM_REFORMA) {
            this.status = StatusOnibus.DISPONIVEL;
            this.historicoRetornoReforma.add(LocalDate.now());
        }
    }

    // <<< LÓGICA REFINADA: Aposentar é o processo final que remove os componentes.
    public void aposentar() {
        if (this.status != StatusOnibus.APOSENTADO) {
            if (this.status == StatusOnibus.EM_OPERACAO) retirarDeOperacao();
            
            removerPneus();
            removerMotor();
            removerCambio();
            
            this.status = StatusOnibus.APOSENTADO;
        }
    }
    
    public void vender() {
        if (this.status == StatusOnibus.APOSENTADO) {
            this.status = StatusOnibus.VENDIDO;
        }
    }
    
    private void removerPneus() {
        // Copia a lista para evitar ConcurrentModificationException durante a remoção
        List<Pneu> pneusAtuais = new ArrayList<>(this.pneus);
        for(Pneu pneu : pneusAtuais) {
            removerPneu(pneu.getPosicao());
        }
    }

    // --- MÉTODOS DE CONSULTA E LÓGICA PREVENTIVA ---

    @Transient
    public LocalDate getDataUltimaManutencao() {
        if (historicoRetornoManutencao.isEmpty()) return null;
        return historicoRetornoManutencao.get(historicoRetornoManutencao.size() - 1);
    }

    @Transient
    public LocalDate getDataUltimaReforma() {
        if (historicoRetornoReforma.isEmpty()) return null;
        return historicoRetornoReforma.get(historicoRetornoReforma.size() - 1);
    }
    
    // <<< NOVOS MÉTODOS PARA LÓGICA PREVENTIVA PROATIVA DO ÔNIBUS >>>
    @Transient
    public Long getDiasRestantesManutencao() {
        if (status == StatusOnibus.APOSENTADO || status == StatusOnibus.VENDIDO) return Long.MAX_VALUE;
        
        LocalDate dataBase = (getDataUltimaManutencao() != null) ? getDataUltimaManutencao() : this.dataCompra;
        
        LocalDate proximaManutencao = dataBase.plusDays(DIAS_LIMITE_MANUTENCAO);
        return ChronoUnit.DAYS.between(LocalDate.now(), proximaManutencao);
    }

    @Transient
    public Long getDiasRestantesReforma() {
        if (status == StatusOnibus.APOSENTADO || status == StatusOnibus.VENDIDO) return Long.MAX_VALUE;
        
        LocalDate dataBase = (getDataUltimaReforma() != null) ? getDataUltimaReforma() : this.dataCompra;
        LocalDate proximaReforma = dataBase.plusDays(DIAS_LIMITE_REFORMA);
        return ChronoUnit.DAYS.between(LocalDate.now(), proximaReforma);
    }

    public boolean manutencaoPrestesVencer() {
        Long dias = getDiasRestantesManutencao();
        return dias != null && dias <= 30;
    }

    public boolean precisaManutencao() {
        Long dias = getDiasRestantesManutencao();
        return dias != null && dias <= 0;
    }

    public boolean reformaPrestesVencer() {
        Long dias = getDiasRestantesReforma();
        return dias != null && dias <= 30;
    }

    public boolean precisaReforma() {
        Long dias = getDiasRestantesReforma();
        return dias != null && dias <= 0;
    }
}
