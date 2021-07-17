package read.code.yourreader.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import read.code.yourreader.R
import read.code.yourreader.databinding.FragmentHomeBinding
import read.code.yourreader.databinding.FragmentSettingsBinding
import read.code.yourreader.di.components.DaggerFactoryComponent
import read.code.yourreader.di.modules.FactoryModule
import read.code.yourreader.di.modules.RepositoryModule
import read.code.yourreader.mvvm.repository.MainRepository
import read.code.yourreader.mvvm.viewmodels.MainViewModel


class SettingsFragment : Fragment() {

    lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var component: DaggerFactoryComponent
    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)

        mAuth= FirebaseAuth.getInstance()
        component = DaggerFactoryComponent.builder()
            .repositoryModule(RepositoryModule(requireContext()))
            .factoryModule(FactoryModule(MainRepository(requireContext())))
            .build() as DaggerFactoryComponent
        viewModel = ViewModelProviders.of(this, component.getFactory())
            .get(MainViewModel::class.java)


        binding.SignOutSettings.setOnClickListener {
            viewModel.signOut()
            requireActivity().finish()
        }

        return binding.root
    }


}