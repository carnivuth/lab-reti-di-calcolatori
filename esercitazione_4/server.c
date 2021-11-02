#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>
#include <errno.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>
#include <arpa/inet.h>
#include <dirent.h>
#include <sys/stat.h>

int main(int argc, char* argv[]){ //args: port
	
	struct hostent *host;
	struct sockaddr_in clientaddr, serveraddr;
	fd_set fdset;

	//controllo argomenti
	if (argc != 2){
		perror("Argomenti invalidi\n");
		exit(1);
	}
	int port = atoi(argv[1]);
	if (port < 1024 || port > 65535){
		perror("Porta invalida\n");
		exit(2);
	}

	//inizializzazione server
	memset((char *)&serveraddr, 0, sizeof(serveraddr));
	serveraddr.sin_family = AF_INET;
//	serveraddr.sin_addr.s_addr = INADDR_ANY;
	serveraddr.sin_addr.s_addr = inet_addr("127.0.0.1");
	serveraddr.sin_port = htons(port);

	//creazione e settaggio socket TCP
	int listen_sd = socket(AF_INET, SOCK_STREAM, 0);
	if (listen_sd < 0){
		perror("Errore creazione socket TCP\n");
		exit(2);
	}
	int on = 1;
	if (setsockopt(listen_sd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on)) < 0){
		perror("Errore di settaggio\n");
	}

	if (bind(listen_sd, (struct sockaddr *)&serveraddr, sizeof(serveraddr)) < 0){
		perror("Errore di binding\n");
		exit(3);
	}
	if (listen(listen_sd, 5) < 0){
		perror("Errore di listen\n");
		exit(4);
	}

	//creazione e settaggio socket UDP
	int udp_sd = socket(AF_INET, SOCK_DGRAM, 0);
	if (udp_sd < 0){
		perror("Errore di creazione socket UDP\n");
		exit(5);
	}
	if (setsockopt(udp_sd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on)) < 0){
		perror("Errore di settaggio socket UDP\n");
		exit(6);
	}
	if (bind(udp_sd, (struct sockaddr *)&serveraddr, sizeof(serveraddr)) < 0){
		perror("Errore di binding UDP\n");
		exit(7);
	}

	int fdsn = ((listen_sd >= udp_sd)? listen_sd : udp_sd) + 1;
	printf("tcp %d udp %d fdsn %d\n", listen_sd, udp_sd, fdsn);
	FD_ZERO(&fdset);	

	int nready, conn_sd;
	printf("server located on %s:%d\n", inet_ntoa(serveraddr.sin_addr), ntohs(serveraddr.sin_port));
	int ris, dimNomeFile, dimParola, i=0, fdr, fdw, dimDir, dimNomeFileNet;
	char tmpChar, tmpParola[256], nomeFile[256], nomeFileTmp[256], parola[256], buff[256], nomeDir[256], zero, dirPath[256];
	int len = sizeof(clientaddr);
	int* dimNomeFileP, dimParolaP;
	char* nomeFileP,parolaP;
	DIR* d, *innerD;
	struct dirent* dir, *innerDir;
	struct stat stats;
	while(1){
		//da eliminare
		/*printf("accepting...\n");
		conn_sd = accept(listen_sd, (struct sockaddr*)&clientaddr, &len);
		printf("tcp accettato\n");
		if (conn_sd < 0){
			perror("Errore di accept\n");
			exit(2);
		}
*/
		FD_SET(listen_sd, &fdset);
		FD_SET(udp_sd, &fdset);
		//printf("fd setted\n");
		if ((nready = select(fdsn,&fdset,NULL,NULL,NULL)) < 0){
			if (errno == EINTR){
				perror("Interruzione da segnale, si riparte\n");
				continue;
			}else{
				perror("Errore nella select\n");
				exit(8);
			}
		}
		//printf("select done, nready %d\n", nready);
		for(int i=0;i<fdsn;i++){
			if (FD_ISSET(i,&fdset)) printf("1");
			else printf("0");
		}
		if (FD_ISSET(udp_sd, &fdset)){
			//gestione richiesta UDP
			printf("RICHIESTA UDP ACCETTATA\n");
/*
			//ricezione dimNomeFile, nomeFile
			recvfrom(udp_sd, &dimNomeFile, sizeof(int),0,(struct sockaddr *)&clientaddr, &len); //la prima con la recvfrom per prendere il client
			dimNomeFile = ntohl(dimNomeFile);
			printf("dimNomeFile: %d\n", dimNomeFile);
			read(udp_sd, nomeFile, dimNomeFile);
			printf("nomeFile: %s\n");

			//ricezione dimParola, parola
			read(udp_sd, &dimParola, sizeof(int));
			dimParola = ntohl(dimParola);
			printf("dimParola: %d\n", dimParola);
			read(udp_sd, parola, dimParola);
			printf("parola: %s\n", parola);
*/
/*
			recvfrom(udp_sd,buff,256,0,(struct sockaddr*)&clientaddr, &len);
			printf("ricevuto\n");
			dimNomeFileP=buff;
			nomeFileP = buff+4;
			dimParolaP = buff+4+dimNomeFile;
			parolaP = buff+4+dimNomeFile+4+dimParola;

			printf("packet: %d %s %d %s\n",dimNomeFile,nomeFile,dimParola,parola);
*/
			recvfrom(udp_sd,buff,256,0,(struct sockaddr*)&clientaddr, &len);
			strcpy(nomeFile,buff);
			strcpy(parola,buff+strlen(nomeFile)+1);
			printf("packet: %s %s\n",nomeFile,parola);

			//lettura e sovrascrittura file
			strcpy(nomeFileTmp, nomeFile);
			strcat(nomeFileTmp,"Tmp");
			printf("file: %s fileTmp: %s\n",nomeFile,nomeFileTmp);
			if((fdr = open(nomeFile, O_RDONLY)) < 0){
				perror("Errore apertura file in lettura\n");
				ris = -1;
				write(udp_sd, &ris, sizeof(int));
			}
			if ((fdw = open(nomeFileTmp, O_WRONLY | O_CREAT)) < 0){
				perror("Errore apertura file in scrittura\n");
				ris = -1;
				write(udp_sd, &ris, sizeof(int));
			}
			printf("AAAA\n");
			ris = 0;
			while(read(fdr, &tmpChar, 1) > 0){
					printf("letto %c\n",tmpChar);
				if (tmpChar != ' ' && tmpChar != '\n'){
					tmpParola[i] = tmpChar;
					i++;
				}else{
					tmpParola[i] = '\0';
					printf("tmpParola: %s\n", tmpParola);
					if (strcmp(tmpParola, parola) == 0){
						ris++;
					}else{
						write(fdw,tmpParola,i);
					}
					if (tmpChar == ' ') write(fdw, " ", 1);
					else write(fdw, "\n", 1);
					i=0;
				}
				
			}
			printf("ris: %d\n", ris);
			if (fork() == 0){
				execl("/bin/mv", "mv", nomeFileTmp, nomeFile, 0);
				perror("errore execl\n");
				exit(-1);
			}

			ris = htonl(ris);
			sendto(udp_sd,&ris,sizeof(int),0,(struct sockaddr*)&clientaddr, len);
			close(fdr);
			close(fdw);
		}

		if (FD_ISSET(listen_sd, &fdset)){
			//gestione richiesta TCP
			printf("RICHIESTA TCP ACCETTATA\n");

			//creazione figlio
			conn_sd = accept(listen_sd, (struct sockaddr*)&clientaddr, &len);
			if (fork() == 0){
				//figlio
				printf("fork started\n");
				//ricezione dimDir e dir
				while(read(conn_sd, &dimDir, sizeof(int)) > 0){
					dimDir = ntohl(dimDir);
					printf("dimDir: %d\n", dimDir);
					for(int i=0; i<dimDir; i++){
						read(conn_sd, &nomeDir[i], 1);
					}
					printf("nomeDir: %s\n", nomeDir);

					//ricerca nomi file
					d = opendir(nomeDir);
					if (d == NULL){
						perror("Impossibile aprire la directory\n");
						dimNomeFile = -1;
						write(conn_sd, &dimNomeFile, sizeof(int)); //notifica errore apertura
						continue;
					}
					while((dir = readdir(d)) != NULL){
						printf("%s\n", dir->d_name);
						if (strcmp(dir->d_name,".") == 0 || strcmp(dir->d_name,"..")==0){
							printf("cartella . o .., da saltare\n");
							continue;
						}
						printf("ok\n");
						strcpy(dirPath,nomeDir);
						strcat(dirPath,"/");
						strcat(dirPath,dir->d_name);
						printf("dirPath: %s\n", dirPath);
						stat(dirPath, &stats);
						if (S_ISDIR(stats.st_mode)){
							innerD = opendir(dirPath);
							while((innerDir = readdir(innerD)) != NULL){
								printf("found: %s\n", innerDir->d_name);
								dimNomeFile = strlen(innerDir->d_name)+1;
								dimNomeFileNet = htonl(dimNomeFile);
								write(conn_sd,&dimNomeFileNet, sizeof(int));
								write(conn_sd,innerDir->d_name,dimNomeFile);
							}

						}
					}
					dimNomeFile = -2;
					dimNomeFileNet = htonl(dimNomeFile);
					write(conn_sd,&dimNomeFileNet,sizeof(int));

				}



				exit(0);
			}












		}

	}

}
