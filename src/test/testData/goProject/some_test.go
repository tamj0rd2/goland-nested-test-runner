package goProject_test

import "testing"

func TestTopLevel(t *testing.T) {
	t.Run("My single subtest", func(t *testing.T) {
		t.FailNow()
	})

	t.Run("My second subtest", func(t *testing.T) {
		t.Run("My nested subtest", func(t *testing.T) {
			t.FailNow()
		})
	})
}
