
#include <stdio.h>
#include "fattore.h"
#include <fcntl.h>
#include <unistd.h>
#include <dirent.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <libgen.h>

#define regSize 256

typedef struct {
	char candidato [256];
	char giudice [256];
	char categoria [2];
	char nomeFile [256];
	char fase [256];
	int voto;
}registro;

static registro reg[regSize];

void inizializza(){
	printf("init\n");
	static int done = 0;

	if (done == 1) return;
	else{
		done = 1;
		printf("soprafor\n");

		for(int i=0; i<regSize; i++){

			printf("nelfor\n");
			
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

		strcpy(reg[2].candidato, "candidato3");
		strcpy(reg[2].giudice, "giudice2");
		strcpy(reg[2].categoria, "O");
		strcpy(reg[2].nomeFile, "/tmp/candidati/file3");
		strcpy(reg[2].fase, "B");
		reg[2].voto = 0;

	}

}



void sortGiudici(char (*giudici)[30], int voti[], int n){
	printf("sorting %d elements\n", n);
	
}
 


int indexOfGiudice(char* giudice, char (*giudici)[30]){
	int index = -1;
	for (int i=0; i<256; i++){
		if (strcmp(giudici[i], "NULL") == 0) break;
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
		printf("AAA giudice: %s\n", giudici[i]);
		if (strcmp(giudici[i], "NULL") == 0){
			printf("posizione libera\n");
			index = i;
			strcpy(giudici[i], giudice);
			break;
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

Output* classifica_giudici_1_svc(void * a, struct svc_req* rq){
	printf("aa\n");
	inizializza();
	printf("bb\n");
	static Output res;

    for (int i = 0; i < 256; i++)
    {
       memset(res.giudici[i].nome,'\0',7);
    }
    
    
    
  
	char giudici[256][30];
	for(int i=0; i<256; i++){
		strcpy(giudici[i], "NULL");
	}

	int voti[256];
	int nGiudici = 0;
	int index, indexPlaced;

	//creazione array paralleli non ordinati
	for (int i=0; i<regSize; i++){
		printf("record registro valido\n");
		if (strcmp(reg[i].giudice, "L") == 0) break;
		if ((index = indexOfGiudice(reg[i].giudice, giudici)) == -1){
			indexPlaced = aggiungiGiudice(reg[i].giudice, giudici); //nella prima posizione libera
			printf("aggiungendo il giudice %s all'indice%d\n", reg[i].giudice, indexPlaced);
			voti[indexPlaced] = reg[indexPlaced].voto;
			printf("voto: %d\n", voti[indexPlaced]);
			nGiudici++;
		}else{
			voti[index] += reg[i].voto;
			printf("voto: %d\n", voti[index]);
		}
			}

	//stampaRegistro(reg);
	for(int i=0; i<256; i++){
		if (strcmp(giudici[i], "NULL") != 0){
			printf("%s\n", giudici[i]);
			printf("voto %d\n", voti[i]);
		}
	}
	//sorting
	int i, j;
	for(i = 0; i < nGiudici - 1; i++) 
		for (j = nGiudici - 1; j > i; j--) 
		if (voti[j] > voti[j-1]){
			int temp;
			temp = voti[j];
			voti[j] = voti[j-1];
			voti[j-1] = temp;
			char temp1[30];
			strcpy(temp1, giudici[j]);
			strcpy(giudici[j],giudici[j-1]);
			strcpy(giudici[j-1],temp1);
		}

	for(int i=0; i<256; i++){
		if (strcmp(giudici[i], "NULL") != 0){
			printf("%s\n", giudici[i]);
			printf("voto: %d\n", voti[i]);
		}
	}

	//inizializzazione e compilazione output
	for(int i=0; i<regSize; i++){
		if (strcmp(giudici[i], "NULL") == 0){
			strcpy(res.giudici[i].nome, "NULL");
		}
	}

	for (int i=0; i<regSize; i++){
		if (strcmp(giudici[i], "NULL") == 0) break;
		printf("sending giudice: %s\n", giudici[i]);
		strcpy(res.giudici[i].nome, giudici[i]);	
	}

	return &res;
}

 void *  esprimi_voto_1_svc(input_esprimi_voto* input, struct svc_req* rq){
	inizializza();
	printf("Votazione tipo %s per %s\n", input->op, input->candidato);
	printf("xxx\n");
	if (strcmp(input->op, "a") == 0){
		//aggiungi
		for (int i=0; i<regSize && strcmp(reg[i].candidato, "L"); i++){
			
			if (strcmp(reg[i].candidato, input->candidato) == 0){
				printf("found\n");
				reg[i].voto++;
				printf("aggiunto\n");
			}

		}
	}else{
		//sottrai
		for (int i=0; i<regSize && strcmp(reg[i].candidato, "L"); i++){
			
			if (strcmp(reg[i].candidato, input->candidato) == 0){
				printf("found\n");
				reg[i].voto--;
				printf("sottratto\n");
			}

		}


	}

}
