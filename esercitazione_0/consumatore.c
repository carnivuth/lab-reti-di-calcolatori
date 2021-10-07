#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#define MAX_STRING_LENGTH 256

// consumatore.c e' un filtro
int main(int argc, char* argv[]){

	char *file_in;
	int c;
	
	//controllo numero argomenti
	if (argc == 3){
        // redirezione input e apertura del file
        close(0);
        file_in = argv[2];
	    
	    if (open(file_in, O_RDONLY)<0){
    		perror("P0: Impossibile aprire il file.");exit(2);
	    }    
	}else if (argc != 2 ){ 
		perror(" numero di argomenti sbagliato"); exit(1);
	} 
	do{
        c=getc(stdin);
        if(strchr(argv[1],c)==NULL && c!=EOF){
            putchar(c);
        }
           
    }while(c!=EOF);
}
