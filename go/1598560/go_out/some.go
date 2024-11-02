package go_out

import (
	"bufio"
	"os"
	"some/c_out"
)

var file *os.File

func Prepare(n int) bool {
	switch n {
	case c_out.CreateFile:
		f, err := os.Create("go_out.txt")
		if err != nil {
			return false
		}
		file = f
	case c_out.OpenDevFile:
		f, err := os.OpenFile("/dev/stdout", os.O_APPEND|os.O_WRONLY, 0644)
		if err != nil {
			return false
		}
		file = f
	case c_out.UseStdStream:
		file = os.Stdout
	}

	return true
}

func CleanUp() {
	if file != os.Stderr && file != os.Stdout {
		file.Close()
	}
}

const LoremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\n"

func Run(n int) {
	out := bufio.NewWriterSize(file, c_out.BufSize)
	for i := 0; i < n; i++ {
		out.WriteString(LoremIpsum)
	}
	out.Flush()
}
