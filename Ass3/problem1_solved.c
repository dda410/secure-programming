#include <stdio.h>

int get_cookie() {
  return 0x41424343;
}
int main() {
    int guess;
    char name[20];
    guess = get_cookie(); /*ABCD*/
    printf("Enter your name..\n");
    fgets(name, sizeof(name), stdin);
    /* fgets is the safe version of gets function. it prevents
     * the buffer where are put the readed characters from overflowing . */
    if (guess == 0x41424344)
      printf("You win %s\n", name);
    else
      printf("Better luck next time %s :(\n", name);
    return 0;
}
