package goProject_test

import "testing"

func TestTopLevel(t *testing.T) {
	t.Run("Subtest", func(t *testing.T) {
		t.Run("Nested subtest", func(t *testing.T) {
			t.Run("Deeply nested subtest", func(t *testing.T) {
			})
		})

		testFuncWithSingleUsage(t)
	})
}

func testFuncWithSingleUsage(t *testing.T) {
	t.Run("Subtest with single usage", func(t *testing.T) {
	})
}
