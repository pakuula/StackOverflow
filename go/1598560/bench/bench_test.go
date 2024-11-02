package bench_test

import (
	"some/c_out"
	"some/go_out"
	"testing"
)

const N_LINES = 10000

func BenchmarkGoout(b *testing.B) {
	for i := 0; i < b.N; i++ {
		if !go_out.Prepare() {
			b.Fatal("failed to open file")
		}
		go_out.Run(N_LINES)
		go_out.CleanUp()
	}
}

func BenchmarkCOutFile(b *testing.B) {
	for i := 0; i < b.N; i++ {
		if !c_out.Prepare(c_out.CREATE_FILE) {
			b.Fatal("failed to open file")
		}
		c_out.Run(N_LINES)
		c_out.CleanUp()
	}
}
func BenchmarkCOutDevStderr(b *testing.B) {
	for i := 0; i < b.N; i++ {
		if !c_out.Prepare(c_out.OPEN_DEV_STDERR) {
			b.Fatal("failed to open file")
		}
		c_out.Run(N_LINES)
		c_out.CleanUp()
	}
}
func BenchmarkCOutStderr(b *testing.B) {
	for i := 0; i < b.N; i++ {
		if !c_out.Prepare(c_out.USE_STDERR) {
			b.Fatal("failed to open file")
		}
		c_out.Run(N_LINES)
		c_out.CleanUp()
	}
}
