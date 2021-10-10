#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#define MAX_STRING_LENGTH 256

// produttore.c NON e' un filtro
int main(int argc, char* argv[]){
	int c, fd, written ;
	char *file_out;
	
	//controllo numero argomenti
	if (argc != 2){ 
		perror(" numero di argomenti sbagliato"); exit(1);
	} 
    
	//controllo apertura file 
	file_out = argv[1];	
	fd = open(file_out, O_WRONLY|O_CREAT|O_TRUNC, 00640);
	
	if (fd < 0){
		perror("Impossibile creare/aprire il file");
		exit(2);
	}
    
	//main loop
    c=getc(stdin);
	
	while(c!=EOF){ 
        written=write(fd, &c,4);
    
	        if(written!=4){
		    	perror("errore nella scrittura sul file");
				close(fd);
			    exit(3);
		    }
       c=getc(stdin); 
    }
	close(fd);
}
