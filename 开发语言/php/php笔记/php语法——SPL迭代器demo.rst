迭代递归结构
===========
我们如何迭代多维数组，或者如何迭代包含自容器的容器呢？如果一个类实现了 ``RecursiveIterator`` 接口，或者实现了 ``IteratorAggregate`` 的 ``getIterator()`` 方法，则该类可以递归迭代。

``RecursiveIteratorIterator`` 是一个具体的类，它使用实现 ``RecursiveIterator`` 接口的类来迭代所有数据。

RecursiveIterator
------------------
它扩展 ``Iterator`` 接口并增加了 ``hasChildren()`` 和 ``getChildren()`` 方法。如果当前元素能够迭代，则 ``hasChildren()`` 返回 **true** 而 ``getChildren()`` 方法返回迭代器，该迭代器必须是 ``RecursiveIterator`` 类型或其子类。

下面是一个继承 ``MyArrayIterator`` 并实现 ``RecursiveIterator`` 接口的类。

.. code-block:: php

    <?php
	class MyArrayIterator implements Iterator {

	    private $a;

	    public function __construct (&$a) {
	        $this->a = $a;
	    }

	    public function valid () {
	        return current($this->a) !== false;
	    }

	    public function next () {
	        next($this->a);
	        return;
	    }

	    public function current () {
	        return current($this->a);
	    }

	    public function rewind () {
	        reset($this->a);
	    }

	    public function key () {
	        return key($this->a);
	    }
	}

	class MyRecursiveIterator extends MyArrayIterator implements RecursiveIterator {

	    public function __construct (array &$a) {
	        parent::__construct($a);
	    }

	    // Returns true if an iterator can be created/returned for the current entry.
	    public function hasChildren () {
	        $current = parent::current();
	        return is_array($current);
	    }

	    // Note: getChildren() must return a RecursiveIterator or class derived from RecursiveIterator for the current entry.
	    public function getChildren () {
	        $subarray = parent::current();
	        return new MyRecursiveIterator($subarray);
	    }
	}
    ?>

为了直接使用 ``MyRecursiveIterator`` ，你需要一个递归函数。

.. code-block:: php

    <?php
	function recursive_iterate (\RecursiveIterator $it) {
	    while ($it->valid()) { // Still more to do?

	        // if there is a subarray or subcontainer that needs to be iterated, we call getChildren() and recurse...
	        if ($it->hasChildren()) {

	            echo $it->key() . " => (";

	            recursive_iterate($it->getChildren()); // recurse. Note: getChildren() must return \RecursiveIterator.

	            echo "),\n";

	        } else {  // the current element is not a nested container, so display its elements

	            // otherwise, print the current key and value.
	            echo $it->key() . " => " . $it->current() . ", ";
	        }

	        $it->next();
	    }
	}

	$a2 = array(0, 1, 2 => array(10, 20, 30), 3, 4, 5 => array(1, 2, 3));

	echo "\n=============\n";

	$mri = new MyRecursiveIterator($a2);

	recursive_iterate($mri);

	// An array containing four elements, the first three are nested subarrays.
	$arr = [[["sitepoint", "phpmaster"]],      // first nested array
	    ["buildmobile", "rubysource"],   // second nested array
	    ["designfestival", "cloudspring"], // third nested array
	    "not a subarray"                     // one-dimensional array of one element
	];

	$mri = new MyRecursiveIterator($arr);

	echo "\n\n";

	recursive_iterate($mri);
    ?>

RecursiveIteratorIterator
-------------------------
下面是用 ``RecursiveIteratorIterator`` 消除了使用自定义函数。

.. code-block:: php

    <?php
	echo "\n===================================\n";
	$it = new RecursiveIteratorIterator(new MyRecursiveIterator($a2));
	foreach ($it as  $key => $value) {
	    // print the curren tkey and value.
	    echo $key . " => " . $value . ", ";
	}
    ?>




