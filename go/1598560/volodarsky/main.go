package main

import (
	"bufio"
	"flag"
	"fmt"
	"os"
)

var (
	flagIter    = flag.Bool("i", false, "use iterator")
	flagNoPrint = flag.Bool("n", false, "no print")
	flagBufSize = flag.Uint("b", 65536, "buf size")
)

func main() {
	flag.Parse()
	args := flag.Args()

	if len(args) == 0 {
		os.Stderr.WriteString("Missing sequence length\n")
		os.Exit(1)
	}
	var n int
	{

		i, err := fmt.Sscanf(args[0], "%d", &n)
		if err != nil {
			fmt.Fprintf(os.Stderr, "failed to parse sequence length: %s\n", err.Error())
			os.Exit(1)
		}
		if i == 0 {
			fmt.Fprintf(os.Stderr, "failed to parse sequence length\n")
			os.Exit(1)
		}
	}

	var bsize = *flagBufSize
	if bsize == 0 {
		bsize = 65536
	}
	buffer := bufio.NewWriterSize(os.Stdout, int(bsize))

	b := make([]byte, n+1)
	b[n] = '\n'

	var doPrint = !*flagNoPrint

	var search func(i int)
	search = func(i int) {
		if i == n {
			if doPrint {
				buffer.Write(b)
			}
		} else {
			for d := '0'; d <= '9'; d++ {
				b[i] = byte(d)
				search(i + 1)
			}
		}
	}
	search(0)
	buffer.Flush()
}
