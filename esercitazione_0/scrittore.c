#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#define MAX_STRING_LENGTH 256

// consumatore.c e' un filtro
int main(int argc, char* argv[]){

	char *file_in;
	int  fd;
	
	
	file_in = argv[1];
	fd = open(file_in, O_WRONLY|O_CREAT|O_TRUNC, 00640);
	if (fd<0){
		perror("P0: Impossibile aprire il file.");
		exit(2);
	}
	char *string="abcdefghijklmnopqrstuvwxyz";
	for(int i=0;i<100000000000;i++){
		write (fd,string,sizeof(char)*strlen(string));
	}
	close(fd);
}
