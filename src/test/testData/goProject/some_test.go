package goProject_test

import "testing"

func TestTopLevelForSingleSubtest(t *testing.T) {
	t.Run("My single subtest", func(t *testing.T) {
		t.FailNow()
	})
}
