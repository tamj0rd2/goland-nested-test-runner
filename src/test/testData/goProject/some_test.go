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

func TestContractFirstUsage(t *testing.T) {
	t.Run("First contract usage", contract{}.RunTests)
}

func TestContractSecondUsage(t *testing.T) {
	t.Run("Second contract usage", contract{}.RunTests)
}

func TestContractThirdUsage(t *testing.T) {
	t.Run("Contract nested usage", contract{}.NestedMethod)
}

func testFuncWithSingleUsage(t *testing.T) {
	t.Run("Subtest with single usage", func(t *testing.T) {
	})
}

func testFuncWithMultipleUsages(t *testing.T) {
	t.Run("Subtest with multiple usages", func(t *testing.T) {
	})
}

type contract struct{}

func (c contract) RunTests(t *testing.T) {
	t.Run("Subtest from a contract", func(t *testing.T) {
	})
}

func (c contract) NestedMethod(t *testing.T) {
	c.RunTests(t)
}
