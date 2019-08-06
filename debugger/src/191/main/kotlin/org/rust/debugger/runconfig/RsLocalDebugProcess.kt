/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.debugger.runconfig

import com.intellij.execution.filters.TextConsoleBuilder
import com.intellij.xdebugger.XDebugSession
import com.jetbrains.cidr.execution.RunParameters
import com.jetbrains.cidr.execution.debugger.CidrLocalDebugProcess
import org.rust.cargo.runconfig.CargoRunStateBase

class RsLocalDebugProcess(
    parameters: RunParameters,
    debugSession: XDebugSession,
    consoleBuilder: TextConsoleBuilder
) : CidrLocalDebugProcess(parameters, debugSession, consoleBuilder) {

    fun setupDebugSession(state: CargoRunStateBase) {
        RsDebugProcessConfigurationHelper(this, state.cargoProject).configure()
    }
}
