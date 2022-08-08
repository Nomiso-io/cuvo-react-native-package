require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name         = package['name']
  s.version      = package['version']
  s.summary      = package['description']
  s.license      = package['license']

  s.authors      = package['author']
  s.homepage     = package['homepage']
  s.platform     = :ios, "9.0"

  s.source       = { :git => "https://github.com/sivachandiran/cuvo-react-native-package.git", :tag => "v#{s.version}" }
  
  s.subspec "Video" do |ss|
    ss.source_files  = "ios/Video/**/*.{h,m,swift}"
    ss.dependency "PromisesSwift"
  end

  s.dependency 'React'

end

