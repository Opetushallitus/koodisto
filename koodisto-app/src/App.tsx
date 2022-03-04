import React from 'react';
import { useAtom } from 'jotai';
import { codesAtom } from './api/codes';

function App() {
    const [data] = useAtom(codesAtom);
    return (
        <div>
            <h1>koodisto</h1>
            {data.map((koodistoRyhma) => {
                return (
                    <div key={koodistoRyhma.id}>
                        <h2>{koodistoRyhma.koodistoRyhmaUri}</h2>
                        {koodistoRyhma.koodistos.map((koodisto) => {
                            return <h3 key={koodisto.koodistoUri}>{koodisto.koodistoUri}</h3>;
                        })}
                    </div>
                );
            })}
        </div>
    );
}

export default App;
