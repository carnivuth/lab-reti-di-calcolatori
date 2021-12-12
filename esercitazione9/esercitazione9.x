struct registro{
	string candidato <256>;
	string giudice <256>;
	string categoria <2>;
	string nomeFile <256>;
	string fase <256>;
	int voto;
};

struct giudice{
	string nome<256>;
};

struct Output{
	struct giudice giudici[256];
};

struct input_esprimi_voto{
	string candidato<256>;
	string op<2>;
};

struct voidInput{
	int a;
};

struct voidOutput{
	int a;
};
program esercitazioneNove{
	version SCANVERS{
		Output classifica_giudici(void) = 1;
		void esprimi_voto(input_esprimi_voto) = 2;
	}=1;
}=0x20000022;
