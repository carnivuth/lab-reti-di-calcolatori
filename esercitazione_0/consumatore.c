#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>
#include <unistd.h>
#define MAX_STRING_LENGTH 256


int main(int argc, char* argv[]){

	char *file_in ,*filter=argv[1];
	int c;
	
        if (argc == 3){
        // redirezione input e apertura del file
                close(0);
                file_in = argv[2];    
	
                if (open(file_in, O_RDONLY)<0){
    	                perror("Impossibile aprire il file.");exit(2);
	        }    
	
        }else if (argc != 2 ){ 
		perror("parametri sbagliati,\n sintassi chiamata: consumatore filter file"); exit(1);
	}
	
        //scrematura caratteri stringa di filtraggio
	char TFilter[MAX_STRING_LENGTH];
        int i=1,j=1;
        TFilter[0]=filter[0];
        
        if (strlen(filter)>1){
        
                while(filter[i]!='\0'){
        
                        if (strchr(TFilter, filter[i])==NULL){
                                TFilter[j]=filter[i];
                                j++;
                        }
                        i++;
                }
                TFilter[i]='\0';
        }
        
        //main loop
        c=getc(stdin);
	
        while(c!=EOF){
        
                if(strchr(TFilter,c)==NULL){
                        putchar(c);
                }
                c=getc(stdin);
        
        }
     

}
