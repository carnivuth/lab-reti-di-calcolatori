struct giudice{
	char nome[256];
};

struct Output{
	struct giudice giudici[256];
};

struct input_esprimi_voto{
	char candidato[256];
	char op[2];
};

program esercitazioneNove{
	version SCANVERS{
		Output classifica_giudici(void) = 1;
		void esprimi_voto(input_esprimi_voto) = 2;
	}=1;
}=0x20000022;