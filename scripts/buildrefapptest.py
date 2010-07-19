import buildrefapp
import unittest

class TestBuildRefApp(unittest.TestCase):

    def test_determine_next_version_number(self):
        available_tags = ["atlassian-refapp-parent-2.3.7.alpha1",
                          "atlassian-refapp-parent-2.3.7.alpha2"]
        full_base_version = "atlassian-refapp-parent-2.3.7.alpha"
        self.assertEqual(buildrefapp.determine_next_version_number_internal(available_tags, full_base_version), 3)
        self.assertEqual(buildrefapp.determine_next_version_number_internal([], full_base_version), 1)

if __name__ == '__main__':
    unittest.main()