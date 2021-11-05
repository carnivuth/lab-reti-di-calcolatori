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
#define LINE_LENGTH 256

int main(int argc, char* argv[]){ //args: port
	
	struct hostent *host;
	struct sockaddr_in clientaddr, serveraddr;
	fd_set fdset;
	char fileName[LINE_LENGTH];
  	char parola[LINE_LENGTH];
  	int fd, fd2,result,i;
  	char buf[LINE_LENGTH];
	char c;

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
	for(;;){
		
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

		if (FD_ISSET(udp_sd, &fdset)){
   len = sizeof(struct sockaddr_in);
    //ricevo il nome del file
    if (recvfrom(udp_sd, fileName, sizeof(fileName), 0, (struct sockaddr * ) & cliaddr, & len) < 0) {
      perror("recvfrom filename \n");
      continue;
    }
    if (recvfrom(udp_sd, parola, sizeof(parola), 0, (struct sockaddr * ) & cliaddr, & len) < 0) {
      perror("recvfrom parola \n");
      continue;
    }

    printf("Ho ricevuto filename: %s e parola: %s\n", fileName, parola);

    //verifico l'esistenza del file
    if ((fd = open(fileName, O_RDONLY)) < 0) {
      printf("Il file %s non esiste\n", fileName);
      result = -1;
    }
    if ((fd2 = open("tmp.txt", O_WRONLY | O_CREAT | O_TRUNC, 0666)) < 0) {
      printf("Errore creazione file temporaneo");
      result = -1;
    }
    if (result != -1) {

      result = 0;
      i = 0;
      while (read(fd, & c, 1) > 0) {
        // printf("-%d:%c",i,c);
        if (c == ' ' || c == '\n') {

          buf[i] = '\0';
          // printf("BUFF: %s\n",buf);

          if (strcmp(parola, buf)) {
            //parola diversa strcmp=1
            buf[i] = c;
            write(fd2, buf, i + 1);
            i = 0;
          } else {
            //caso in cui la parola sia stata trovata strcmp=0
            result++;
            i = 0;
            write(fd2, & c, 1);
          }
        } else {
          buf[i] = c;
          i++;
        }
      }
      buf[i] = '\0';
      // printf("BUFF: %s\n",buf);
      if (strcmp(parola, buf)) {
        //parola diversa strcmp=1
        write(fd2, buf, i);
        i = 0;
      } else {
        result++;
        i = 0;
      }
    }
    printf("Il file %s esiste, il risultato e' %d\n", fileName, result);

    //invio risposta
    if (sendto(udp_sd, & result, sizeof(result), 0, (struct sockaddr * ) & cliaddr, len) < 0) {
      perror("sendto \n");
      continue;
    }
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