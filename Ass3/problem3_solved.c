#include <limits.h>
#include <stdio.h>
#include <stdlib.h>

int main(int argc, char *argv[]) {
  unsigned short a, b, c;
  scanf("%hu %hu", &a, &b);
  /* The following statement checks whether an integer overflow can occur
   * In the case the two input values sum will be greater than the maximum 
   * value that can be represented by an unsigned short: 65535 */
  if (a > USHRT_MAX - b || b > USHRT_MAX - a) {
    abort();
  }
  /* The following operation is now safe */
  c = a + b;
  malloc(c);
  return 0;
}
