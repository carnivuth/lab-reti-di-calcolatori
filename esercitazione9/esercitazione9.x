struct registro{
	char candidato [256];
	char giudice [256];
	char categoria [2];
	char nomeFile [256];
	char fase [256];
	int voto;
};

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

struct voidInput{
	int a;
};

struct voidOutput{
	int a;
};
program esercitazioneNove{
	version SCANVERS{
		Output classifica_giudici(voidInput) = 1;
		voidOutput esprimi_voto(input_esprimi_voto) = 2;
	}=1;
}=0x20000022;
