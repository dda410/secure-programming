/* The offby1.c code */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void get_name(char * name, char * pr) {
  char local[20];
  char ch;
  memset(local, 0, sizeof(local));
  printf("%s:", pr);
  fgets(local, sizeof(local), stdin);
  /* fgets is the safe version of gets function. it prevents
   * the buffer where are put the readed characters from overflowing . */
  while ((ch = getchar()) != '\n' && ch != EOF) {
    /* Flushing the remaining input in order to prevent to */
    /* be read by the next ffgets */
  }
  strncat(name, local, strlen(local));
}

int foo() {
  char name[28] = "Hello..";
  char secret[12] = "TOP SECRET";
  char buf[24];
  char n1[] = "Enter your name";
  char n2[] = "Enter Secret code";
  get_name(name, n1);
  memset(buf, 0, sizeof(buf));
  /* sizeof(buf) is changed to sizeof(buf) -1 in order to avoid off by one error.
   * This in fact can lead to an overflow vulnerability that can be exploited by 
   * a malicious user in order to gain full control of the program. */
  strncpy(buf, name, sizeof(buf)-1);
  printf("%s\n", buf);
  memset(name, 0, sizeof(name));
  get_name(name, n2);
  if (strncmp(secret, name, 10) == 0)
    printf("Welcome and %s\n", buf);
  else
    printf("Wrong code, better try again..\n");
  return 0;
}
int main(int argc, char * * argv) {
  foo();
  printf("Bye\n");
  return 0;
}
