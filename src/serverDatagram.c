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

#define LINE_LENGTH 256

int main(int argc, char ** argv) {
  int sd, port, len, result = 0, i = 0;
  const int on = 1;
  struct sockaddr_in cliaddr, servaddr;
  struct hostent * clienthost;
  char fileName[LINE_LENGTH];
  char parola[LINE_LENGTH];
  int fd, fd2;
  char buf[LINE_LENGTH];

  /* CONTROLLO ARGOMENTI ---------------------------------- */
  if (argc != 2) {
    printf("Error: %s port\n", argv[0]);
    exit(1);
  } else {
    while (argv[1][i] != '\0') {
      if ((argv[1][i] < '0') || (argv[1][i] > '9')) {
        printf("Secondo argomento non intero\n");
        printf("Error: %s port\n", argv[0]);
        exit(2);
      }
      i++;
    }
    port = atoi(argv[1]);
    if (port < 1024 || port > 65535) {
      printf("Error: %s port\n", argv[0]);
      printf("1024 <= port <= 65535\n");
      exit(2);
    }
  }

  /* INIZIALIZZAZIONE INDIRIZZO SERVER ---------------------------------- */
  memset((char * ) & servaddr, 0, sizeof(servaddr));
  servaddr.sin_family = AF_INET;
  servaddr.sin_addr.s_addr = INADDR_ANY;
  servaddr.sin_port = htons(port);

  /* CREAZIONE, SETAGGIO OPZIONI E CONNESSIONE SOCKET -------------------- */
  sd = socket(AF_INET, SOCK_DGRAM, 0);
  if (sd < 0) {
    perror("creazione socket ");
    exit(1);
  }
  printf("Server: creata la socket, sd=%d\n", sd);

  if (setsockopt(sd, SOL_SOCKET, SO_REUSEADDR, & on, sizeof(on)) < 0) {
    perror("set opzioni socket ");
    exit(1);
  }
  printf("Server: set opzioni socket ok\n");

  if (bind(sd, (struct sockaddr * ) & servaddr, sizeof(servaddr)) < 0) {
    perror("bind socket ");
    exit(1);
  }
  printf("Server: bind socket ok\n");

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
  //corpo del server
  for (;;) {
    len = sizeof(struct sockaddr_in);
    //ricevo il nome del file
    if (recvfrom(sd, fileName, sizeof(fileName), 0, (struct sockaddr * ) & cliaddr, & len) < 0) {
      perror("recvfrom filename \n");
      continue;
    }
    if (recvfrom(sd, parola, sizeof(parola), 0, (struct sockaddr * ) & cliaddr, & len) < 0) {
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

      int tmp = 0;
      char c;
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
    if (sendto(sd, & result, sizeof(result), 0, (struct sockaddr * ) & cliaddr, len) < 0) {
      perror("sendto \n");
      continue;
    }
  }

}