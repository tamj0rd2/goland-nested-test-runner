package goProject_test

import "testing"

func TestTopLevel(t *testing.T) {
	t.Run("Subtest", func(t *testing.T) {
		t.Run("Nested subtest", func(t *testing.T) {
			t.Run("Deeply nested subtest", func(t *testing.T) {
				t.FailNow()
			})

			t.FailNow()
		})

		t.FailNow()
	})
}
