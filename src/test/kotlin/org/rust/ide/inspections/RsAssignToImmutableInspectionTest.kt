/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.ide.inspections

import org.rust.ExpandMacros
import org.rust.ProjectDescriptor
import org.rust.WithStdlibRustProjectDescriptor
import org.rust.lang.core.macros.MacroExpansionScope

class RsAssignToImmutableInspectionTest : RsInspectionsTestBase(RsAssignToImmutableInspection::class) {

    fun `test E0594 assign to immutable borrowed content`() = checkByText("""
        fn main() {
            let mut y = 1;
            let x = &y;
            <error descr="Cannot assign to immutable borrowed content [E0594]">*x = 2</error>;
        }
    """)

    fun `test E0594 assign to immutable borrowed content with parentheses`() = checkByText("""
        fn main() {
            let mut y = 1;
            let x = &y;
            <error descr="Cannot assign to immutable borrowed content [E0594]">((*x)) = 2</error>;
        }
    """)

    fun `test E0594 assign to immutable borrowed content of literal`() = checkByText("""
        fn main() {
            let foo = &16;
            <error descr="Cannot assign to immutable borrowed content [E0594]">*foo = 32</error>;
        }
    """)

    fun `test E0594 assign to mutable field of immutable binding`() = checkByText("""
        struct Foo { a: i32 }
        fn main() {
            let mut foo = Foo { a: 1 };
            let x = &foo;
            <error descr="Cannot assign to field of immutable binding [E0594]">x.a = 2</error>;
        }
    """)

    fun `test E0594 assign to immutable field of immutable binding`() = checkByText("""
        struct Foo { a: i32 }
        fn main() {
            let foo = Foo { a: 1 };
            let x = &foo;
            <error descr="Cannot assign to field of immutable binding [E0594]">x.a = 2</error>;
        }
    """)

    fun `test E0594 assign to field of immutable binding`() = checkByText("""
        struct Foo { a: i32 }
        fn main() {
            let foo = Foo { a: 1 };
            <error descr="Cannot assign to field of immutable binding [E0594]">foo.a = 2</error>;
        }
    """)

    @ProjectDescriptor(WithStdlibRustProjectDescriptor::class)
    @ExpandMacros(MacroExpansionScope.ALL, "std")
    fun `test E0594 assign to field of immutable binding while iterating`() = checkByText("""
        struct Foo { a: i32 }
        fn main() {
            let mut foos: [Foo; 1] = [Foo { a: 1 }];
            for foo in foos.iter() {
                <error descr="Cannot assign to field of immutable binding [E0594]">foo.a = 2</error>;
            }
        }
    """)

    @ProjectDescriptor(WithStdlibRustProjectDescriptor::class)
    fun `test E0594 assign to indexed content of immutable binding`() = checkByText("""
        fn main() {
            let a: [i32; 3] = [0; 3];
            <error descr="Cannot assign to indexed content of immutable binding [E0594]">a[1] = 5</error>;
        }
    """)

    fun `test E0594 assign to immutable dereference of raw pointer`() = checkByText("""
        struct Foo { a: i32 }
        fn main() {}
        unsafe fn f() {
            let x = 5;
            let p = &x as *const i32;
            <error descr="Cannot assign to immutable dereference of raw pointer [E0594]">*p = 1</error>;
        }
    """)

    fun `test assign to index expr of unknown type`() = checkByText("""
        fn main() {
            let xs = &mut unknownType;
            xs[0] = 1;
        }
    """)

    fun `test assign to field of expr of unknown type`() = checkByText("""
        fn foo(x: &mut Unknown) {
            x.a = 1;
        }
    """)

    @ProjectDescriptor(WithStdlibRustProjectDescriptor::class)
    fun `test no E0594 for mutable generic slice of Copy types`() = checkByText("""
        fn swap<T>(index_a: usize, index_b: usize, arr: &mut [T]) where T: Copy {
            let temp = arr[index_a];
            arr[index_a] = arr[index_b];
            arr[index_b] = temp;
        }
    """)
}
