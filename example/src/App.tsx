import React, { useEffect } from 'react'
import RNCuvoPackage, { Counter } from 'cuvo-react-native-package'

const App = () => {
  useEffect(() => {
    console.log(RNCuvoPackage)
  })

  return <Counter />
}

export default App
