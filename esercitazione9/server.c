#include <stdio.h>
#include "esercitazione9.h"
#include <fcntl.h>
#include <unistd.h>
#include <dirent.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <libgen.h>

#define regSize 256

static registro reg[regSize];

void inizializza(){
	printf("init\n");
	static int done = 0;
	if (done == 1) return;
	else{
		done = 1:
		for(int i=0; i<regSize; i++){
			strcpy(reg[i].candidato, "L");
			strcpy(reg[i].giudice, "L");
			strcpy(reg[i].categoria, "L");
			strcpy(reg[i].nomeFile, "L");
			strcpy(reg[i].fase, "L");
			reg[i].voto = -1;
		}

		strcpy(reg[0].candidato, "candidato1");
		strcpy(reg[0].giudice, "giudice1");
		strcpy(reg[0].categoria, "O");
		strcpy(reg[0].nomeFile, "/tmp/candidati/file1");
		strcpy(reg[0].fase, "B");
		reg[0].voto = 0;

		strcpy(reg[1].candidato, "candidato2");
		strcpy(reg[1].giudice, "giudice2");
		strcpy(reg[1].categoria, "M");
		strcpy(reg[1].nomeFile, "/tmp/candidati/file2");
		strcpy(reg[1].fase, "B");
		reg[1].voto = 0;

		strcpy(reg[2].candidato, "candidato2");
		strcpy(reg[2].giudice, "giudice2");
		strcpy(reg[2].categoria, "O");
		strcpy(reg[2].nomeFile, "/tmp/candidati/file3");
		strcpy(reg[2].fase, "B");
		reg[2].voto = 0;

	}

}

void scambia(int *a, int *b){ 
	int temp;
	temp = *a;
	*a = *b;
	*b = temp;
}

void scambia2(char (*a)[30], char (*b)[30]){
	char (*temp)[30];
	temp = a;
	a = b;
	b = temp;
}

void sortGiudici(char (*giudici)[30], int voti[], int n){
	int i, j;
	for(i = 0; i < n - 1; i++) 
		for (j = n - 1; j > i; j--) 
		if (voti[j] < voti[j-1]){
			scambia(&(voti[j]),&(voti[j-1]));
			scambia2(&(giudici[j]),&(giudici[j-1])); //va bene anche per string??
		}
}
 


int indexOfGiudice(char* giudice, char (*giudici)[30]){
	int index = -1;
	for (int i=0; i<256; i++){
		if (giudici[i] == NULL) break;
		if (strcmp(giudice, giudici[i]) == 0){
			index = i;
			break;
		}
	}

	return index;
}

int aggiungiGiudice (char* giudice, char (*giudici)[30]){
	int index = -1;
	for (int i=0; i<256; i++){
		if (giudici[i] == NULL){
			index = i;
			strcpy(giudici[i], giudice);
		}
	}

	return index;
}

void stampaRegistro(){
	printf("stampa\n");
	for (int i=0; i<regSize; i++){
			printf("row:\n");
		if (strcmp(reg[i].giudice, "L") == 0) break;
		printf("%s %s %s %s %s %d\n", reg[i].candidato, reg[i].giudice, reg[i].categoria, reg[i].nomeFile, reg[i].fase, reg[i].voto);

	}
}

Output* classifica_giudici_1_svc(voidInput* a, struct svc_req* rq){
	printf("aa\n");
	inizializza();
	printf("bb\n");
	static Output res;
	char giudici[256][30];
	int voti[256];
	int nGiudici = 0;
	int index, indexPlaced;
	for (int i=0; i<regSize; i++){
		if (strcmp(reg[i].giudice, "L") == 0) break;
		if ((index = indexOfGiudice(reg[i].giudice, giudici)) == -1){
			indexPlaced = aggiungiGiudice(reg[i].giudice, giudici); //nella prima posizione libera
			voti[indexPlaced] = reg[indexPlaced].voto;
			nGiudici++;
		}else{
			voti[index] += reg[i].voto;
		}
	}

	stampaRegistro(reg);

	sortGiudici(giudici, voti, nGiudici);
	for (int i=0; i<regSize; i++){
		if (strcmp(reg[i].giudice, "L") == 0) break;
		strcpy(res.giudici[i].nome, giudici[i]);	
	}

	stampaRegistro(reg);

	return &res;
}

void* esprimi_voto_1_svc(input_esprimi_voto* input, struct svc_req* rq){
	
	printf("Votazione tipo %s per %s\n", input->op, input->candidato);
	printf("xxx\n");
	if (strcmp(input->op, "a") == 0){
		//aggiungi
		printf("yyy\n");
		for (int i=0; i<regSize; i++){
			if (strcmp(reg[i].candidato, "L") == 0){
				printf("Candidato non trovato\n");
				break;
			}
			if (strcmp(reg[i].candidato, input->candidato) == 0){
				printf("found\n");
				reg[i].voto++;
				printf("aggiunto\n");
			}

		}
	}else{
		//sottrai
		for (int i=0; i<regSize; i++){
			if (strcmp(reg[i].candidato, "L") == 0){
				if (strcmp(reg[i].candidato, input->candidato) == 0){
					reg[i].voto--;
					printf("sottratto\n");
				}
			//strcpy(giudici[i], reg[i].giudice);
			}
		}

	}


}





