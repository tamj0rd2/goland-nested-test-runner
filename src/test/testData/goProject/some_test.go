package goProject_test

import "testing"

func TestTopLevel(t *testing.T) {
	t.Run("Subtest", func(t *testing.T) {
		t.Run("Nested subtest", func(t *testing.T) {
			t.Run("Deeply nested subtest", func(t *testing.T) {
			})
		})

		testFuncWithSingleUsage(t)

		t.Run("First usage", testFuncWithMultipleUsages)
		t.Run("Second usage", testFuncWithMultipleUsages)
	})
}

func testFuncWithSingleUsage(t *testing.T) {
	t.Run("Subtest with single usage", func(t *testing.T) {
	})
}

func testFuncWithMultipleUsages(t *testing.T) {
	t.Run("Subtest with multiple usages", func(t *testing.T) {
	})
}
