#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#define MAX_STRING_LENGTH 256

// produttore.c NON e' un filtro
int main(int argc, char* argv[]){
	int fd, written ;
	char *file_out;
	//controllo numero argomenti
	if (argc != 2){ 
		perror(" numero di argomenti sbagliato"); exit(1);
	} 
    //controllo apertura file 
	file_out = argv[1];	
	fd = open(file_out, O_WRONLY|O_CREAT|O_TRUNC, 00640);
	if (fd < 0){
		perror("P0: Impossibile creare/aprire il file");
		exit(2);
	}int c;
   //main loop
	do{
        c=getc(stdin);
        //controllo per non scrivere EOF
        if(c!=EOF){
        written=write(fd, &c,4);
            if(written!=4){
		    	perror("P0: errore nella scrittura sul file");
			    exit(3);
		    }
        }
    }while(c!=EOF);
	close(fd);
}
