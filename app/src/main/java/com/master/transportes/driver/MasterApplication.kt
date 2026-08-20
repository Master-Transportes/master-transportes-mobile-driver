package com.master.transportes.driver

import android.app.Application
import com.master.transportes.driver.core.location.LocationUploader
import com.master.transportes.driver.feature.driver.domain.SessionBootstrapStarter
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MasterApplication : Application() {

    /**
     * Ponto de injeção do SessionBootstrapStarter: o Hilt cria o singleton já
     * no arranque (field injection é o gatilho) e o init do Starter registra os
     * collectors de sessão — bootstrap no login/cold start e limpeza no logout.
     */
    @Inject
    lateinit var sessionBootstrapStarter: SessionBootstrapStarter

    /**
     * Ponto de injeção do LocationUploader: garante que o orquestrador de
     * envio de localização exista no arranque — o init dele observa o status
     * online do motorista e liga/desliga o upload do GPS.
     */
    @Inject
    lateinit var locationUploader: LocationUploader
}