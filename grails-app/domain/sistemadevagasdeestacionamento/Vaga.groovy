package sistemadevagasdeestacionamento

class Vaga {
    String numero
    String setor
    static hasMany = [reservas:Reserva]
    String preferenceType
    boolean ocupada

    Vaga(){
        reservas = []
    }

    static constraints = {
        numero nullable: false, blank: false, unique: true
        setor inList: ["CIn", "CCEN", "Area II"]
        preferenceType inList: ["Normal", "Deficiente", "Idoso"]
        ocupada nullable: false
    }

    static Vaga sugestaoVaga (User usuario) {
                def setor = usuario.preferredSector
                def tipo = usuario.preferenceType
                def vaga = findBySetorAndPreferenceTypeAndOcupada(setor,tipo,false)
                if( vaga == null) {
                        vaga = findByOcupada(false)
                    }
                return vaga
            }

    def ocupar(User usuarioLogado){
        this.setOcupada(true)
        def reserva = new Reserva(usuario: usuarioLogado, vaga: this, entrada: new Date())
        this.reservas.add(reserva)
    }

    def desocupar(){
        this.setOcupada(false)
        this.reservas.last().setSaida(new Date())
    }

    def desocuparAposTempo(int tempo){
        def tempoAtual = new Date()
        if (this.reservas != null && this.getOcupada()) {
            def tempoDecorrido = tempoAtual.time - (this.reservas.last().entrada.time + (tempo * 1000))
            if (tempoDecorrido >= 0) {
                this.desocupar()
            }
        }
    }
}