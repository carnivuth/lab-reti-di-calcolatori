#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>
#include <fcntl.h>

int main(int argc, char* argv[]){ //args: ipServer portServer
	
	struct hostent* host;
	struct sockaddr_in serveraddr;
	
	//controllo argomenti
	if (argc != 3){
		perror("Argomenti invalidi\n");
		exit(1);
	}
	int port = atoi(argv[2]);
	if (port < 1024 || port > 65535){
		perror("Porta invalida\n");
		exit(2);
	}
	host = gethostbyname(argv[1]);
	if (host == NULL){
		perror("Host invalido\n");
		exit(3);
	}
	//inizializzazione server
	memset((char*)&serveraddr, 0, sizeof(serveraddr));
	serveraddr.sin_family = AF_INET;
	serveraddr.sin_addr.s_addr = ((struct in_addr*)(host->h_addr))->s_addr;
	serveraddr.sin_port = htons(port);

	//creazione socket e binding
	int sd = socket(AF_INET, SOCK_STREAM, 0);
	if (sd < 0){
		perror("Errore di creazione socket\n");
		exit(4);
	}

	if (connect(sd, (struct sockaddr*)&serveraddr, sizeof(struct sockaddr)) < 0){
		perror("Errore di binding (connect)\n");
		exit(5);
	}

/*	if (bind(sd, (struct sockaddr*)&serveraddr, sizeof(struct sockaddr)) < 0){
		perror("Errore di binding\n");
		exit(5);
	}
*/	
	char nomeDir[256], nomeFile[256];
	int dimNomeDir, dimNomeDirNet, dimNomeFile;

	printf("Inserisci nome directory\n");
	while(scanf("%s", nomeDir) > 0){
		//invio dimNomeDir e nomeDir
		dimNomeDir = strlen(nomeDir)+1; //\0 incluso
		dimNomeDirNet = htonl(dimNomeDir);
		write(sd, &dimNomeDirNet, sizeof(int));
		write(sd, nomeDir, dimNomeDir);

		//ricezione risposta: dimNome1, file1, ..., dimNomeN, fileN, -2 (da cambiare nel server)
		read(sd, &dimNomeFile, sizeof(int));
		dimNomeFile = ntohl(dimNomeFile);
		printf("dimNomeFile: %d\n", dimNomeFile);

		while(dimNomeFile != -2){
			for(int i=0; i<dimNomeFile; i++){
				read(sd, &nomeFile[i], 1);
			}
			printf("nomeFile: %s\n", nomeFile);

			read(sd, &dimNomeFile, sizeof(int));
			dimNomeFile = ntohl(dimNomeFile);
			printf("dimNomeFile: %d\n", dimNomeFile);
		}

		printf("Inserisci nome directory\n");
		
	}
	



}
