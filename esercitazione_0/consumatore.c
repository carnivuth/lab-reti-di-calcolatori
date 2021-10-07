#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>
#include <unistd.h>
#define MAX_STRING_LENGTH 256

// consumatore.c e' un filtro
int main(int argc, char* argv[]){

	char *file_in ,*filter=argv[1];
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
        float times= (float)time(NULL);
	//scrematura caratteri stringa di filtraggio
	 char output[MAX_STRING_LENGTH];
        int i=1,j=1;
       // if (strlen(filter)==0) return 0;
        output[0]=filter[0];
        if (strlen(filter)>1){
                while(filter[i]!='\0'){
                        if (strchr(output, filter[i])==NULL){
                                output[j]=filter[i];
                                j++;
                        }
                        i++;
                }
                output[i]='\0';
        }

	

	do{
        c=getc(stdin);
        if(strchr(output,c)==NULL && c!=EOF){
            putchar(c);
        }
        
    }while(c!=EOF);
    printf("%f",(float)time(NULL)-times);   
	printf("%s",output);
}
